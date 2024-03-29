package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ConcurrentReferenceHashMap;
import usi.si.seart.exception.TaskFailedException;
import usi.si.seart.exception.TaskNotFoundException;
import usi.si.seart.model.task.CodeTask;
import usi.si.seart.model.task.Status;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.task.processing.CodeProcessing;
import usi.si.seart.model.task.processing.Processing;
import usi.si.seart.model.task.query.CodeQuery;
import usi.si.seart.model.task.query.Query;
import usi.si.seart.model.user.User;
import usi.si.seart.repository.TaskRepository;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface TaskService {

    boolean canCreateTask(User user, Integer limit);
    boolean activeTaskExists(User user, Query query, Processing processing);
    void create(User requester, LocalDateTime requestedAt, CodeQuery query, CodeProcessing processing);
    <T extends Task> T update(T task);
    <T extends Task> void cancel(T task);
    void registerException(TaskFailedException ex);
    void forEachNonExpired(Consumer<Task> consumer);
    void forEachExecuting(Consumer<Task> consumer);
    Optional<Task> getNext();
    Page<Task> getAll(Specification<Task> specification, Pageable pageable);
    Task getWithUUID(UUID uuid);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class TaskServiceImpl implements TaskService {

        TaskRepository taskRepository;

        TaskLockMap taskLockMap = new TaskLockMap();

        private static class TaskLockMap {

            private final ConcurrentReferenceHashMap<Long, Lock> taskLocks = new ConcurrentReferenceHashMap<>();

            private Lock getLock(Task task) {
                return this.taskLocks.compute(task.getId(), (k, v) -> v == null ? new ReentrantLock() : v);
            }
        }

        @Override
        public boolean canCreateTask(User user, Integer limit) {
            Long activeTasks = taskRepository.countAllByUserAndStatusIn(user, Status.Category.ACTIVE);
            return activeTasks < limit;
        }

        @Override
        public boolean activeTaskExists(User user, Query query, Processing processing) {
            int taskHash = Objects.hash(user, query, processing);
            return taskRepository.findAllByUserAndStatusIn(user, Status.Category.ACTIVE)
                    .stream()
                    .mapToInt(Task::hashCode)
                    .anyMatch(hash -> hash == taskHash);
        }

        @Override
        public void create(User requester, LocalDateTime requestedAt, CodeQuery query, CodeProcessing processing) {
            Task task = CodeTask.builder()
                    .user(requester)
                    .submitted(requestedAt)
                    .query(query)
                    .processing(processing)
                    .build();

            query.setTask(task);
            processing.setTask(task);
            taskRepository.save(task);
        }

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public <T extends Task> T update(T task) {
            Lock taskLock = taskLockMap.getLock(task);
            try {
                taskLock.lock();
                return taskRepository.saveAndFlush(task);
            } finally {
                taskLock.unlock();
            }
        }

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public <T extends Task> void cancel(T task) {
            Lock taskLock = taskLockMap.getLock(task);
            try {
                taskLock.lock();
                taskRepository.markForCancellation(task.getId());
            } finally {
                taskLock.unlock();
            }
        }

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void registerException(TaskFailedException ex) {
            Task task = ex.getTask();
            Throwable cause = ex.getCause();

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            cause.printStackTrace(printWriter);
            String stackTrace = stringWriter.toString();

            task.setStatus(Status.ERROR);
            task.setFinished(LocalDateTime.now(ZoneOffset.UTC));
            task.setExpired(true);
            task.setErrorStackTrace(stackTrace);

            taskRepository.saveAndFlush(task);
        }

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void forEachNonExpired(Consumer<Task> consumer) {
            LocalDateTime oneWeekAgo = LocalDateTime.now(ZoneOffset.UTC).minusWeeks(1);
            @Cleanup Stream<Task> taskStream = taskRepository.findAllByFinishedLessThanAndExpired(oneWeekAgo, false);
            taskStream.forEach(consumer);
        }

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void forEachExecuting(Consumer<Task> consumer) {
            taskRepository.findAllByStatus(Status.EXECUTING).forEach(consumer);
        }

        @Override
        @Transactional
        public Optional<Task> getNext() {
            return taskRepository.findFirstByStatusOrderBySubmitted(Status.QUEUED)
                    .map(task -> {
                        task.setStatus(Status.EXECUTING);
                        if (task.getStarted() == null)
                            task.setStarted(LocalDateTime.now(ZoneOffset.UTC));
                        return task;
                    });
        }

        public Page<Task> getAll(Specification<Task> specification, Pageable pageable) {
            return taskRepository.findAll(specification, pageable);
        }

        @Override
        public Task getWithUUID(UUID uuid) {
            return taskRepository.findByUuid(uuid)
                    .orElseThrow(() -> new TaskNotFoundException("uuid", uuid));
        }
    }
}
