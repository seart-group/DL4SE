package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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

import javax.persistence.Tuple;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
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
    List<Task> getAll(Integer page, Integer pageSize, String column);
    Task getWithUUID(UUID uuid);
    Map<Status, Long> getSummary();
    Map<Status, Long> getSummary(User user);

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

        @Override
        public List<Task> getAll(Integer page, Integer pageSize, String column) {
            Sort sort = Sort.by(column).ascending();
            Pageable pageable = PageRequest.of(page, pageSize, sort);
            return taskRepository.findAll(pageable).getContent();
        }

        @Override
        public Task getWithUUID(UUID uuid) {
            return taskRepository.findByUuid(uuid)
                    .orElseThrow(() -> new TaskNotFoundException("uuid", uuid));
        }

        @Override
        public Map<Status, Long> getSummary() {
            Supplier<List<Tuple>> countQuery = taskRepository::countAllGroupByStatus;
            return supplyToMap(countQuery, Status.class, Long.class);
        }

        @Override
        public Map<Status, Long> getSummary(User user) {
            Supplier<List<Tuple>> userCountQuery = () -> taskRepository.countAllByUserGroupByStatus(user);
            return supplyToMap(userCountQuery, Status.class, Long.class);
        }

        private <K, V> Map<K, V> supplyToMap(
                Supplier<List<Tuple>> tupleResultQuery, Class<K> keyType, Class<V> valueType
        ) {
            return tupleResultQuery.get().stream()
                    .map(tuple -> Map.entry(tuple.get(0, keyType), tuple.get(1, valueType)))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }
    }
}
