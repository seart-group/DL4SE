package ch.usi.si.seart.server.service;

import ch.usi.si.seart.exception.TaskNotFoundException;
import ch.usi.si.seart.model.job.Job;
import ch.usi.si.seart.model.task.Status;
import ch.usi.si.seart.model.task.Task;
import ch.usi.si.seart.model.task.Task_;
import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.server.exception.TaskFailedException;
import ch.usi.si.seart.server.repository.TaskRepository;
import com.fasterxml.jackson.databind.JsonNode;
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
    boolean activeTaskExists(User user, Job dataset, JsonNode query, JsonNode processing);
    void create(User requester, Job dataset, JsonNode query, JsonNode processing, LocalDateTime requestedAt);
    Task update(Task task);
    void cancel(Task task);
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
        public boolean activeTaskExists(User user, Job dataset, JsonNode query, JsonNode processing) {
            int taskHash = Objects.hash(user, dataset, query, processing);
            return taskRepository.findAllByUserAndStatusIn(user, Status.Category.ACTIVE)
                    .stream()
                    .mapToInt(Task::hashCode)
                    .anyMatch(hash -> hash == taskHash);
        }

        @Override
        public void create(
                User requester, Job dataset, JsonNode query, JsonNode processing, LocalDateTime requestedAt
        ) {
            taskRepository.save(
                    Task.builder()
                            .user(requester)
                            .dataset(dataset)
                            .query(query)
                            .processing(processing)
                            .submitted(requestedAt)
                            .build()
            );
        }

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public Task update(Task task) {
            Lock taskLock = taskLockMap.getLock(task);
            taskLock.lock();
            try {
                return taskRepository.saveAndFlush(task);
            } finally {
                taskLock.unlock();
            }
        }

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void cancel(Task task) {
            Lock taskLock = taskLockMap.getLock(task);
            taskLock.lock();
            try {
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
                    .orElseThrow(() -> new TaskNotFoundException(Task_.uuid, uuid));
        }
    }
}
