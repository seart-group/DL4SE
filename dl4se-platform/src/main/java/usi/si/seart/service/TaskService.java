package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import usi.si.seart.model.task.CodeTask;
import usi.si.seart.model.task.Status;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.task.processing.CodeProcessing;
import usi.si.seart.model.task.processing.Processing;
import usi.si.seart.model.task.query.CodeQuery;
import usi.si.seart.model.task.query.Query;
import usi.si.seart.model.user.User;
import usi.si.seart.repository.TaskRepository;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

public interface TaskService {

    boolean canCreateTask(User user);
    boolean activeTaskExists(User user, Query query, Processing processing);
    void create(User requester, LocalDateTime requestedAt, CodeQuery query, CodeProcessing processing);
    <T extends Task> T update(T task);
    <T extends Task> void cancel(T task);
    void forEachNonExpired(Consumer<Task> consumer);
    Optional<Task> getNext();
    Optional<Task> getWithUUID(UUID uuid);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    class TaskServiceImpl implements TaskService {

        TaskRepository taskRepository;

        @NonFinal
        @Value("${app.request.limit}")
        Long userTaskLimit;

        @Override
        public boolean canCreateTask(User user) {
            Long activeTasks = taskRepository.countAllByUserAndStatusIn(user, Status.Category.ACTIVE);
            return activeTasks < userTaskLimit;
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

        Object lock = new Object();

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public <T extends Task> T update(T task) {
            synchronized (lock) {
                return taskRepository.saveAndFlush(task);
            }
        }

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public <T extends Task> void cancel(T task) {
            synchronized (lock) {
                taskRepository.markForCancellation(task.getId());
            }
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
        public Optional<Task> getNext() {
            return taskRepository.findFirstExecuting()
                    .or(() -> {
                        taskRepository.markForExecution();
                        return taskRepository.findFirstExecuting();
                    });
        }

        @Override
        public Optional<Task> getWithUUID(UUID uuid) {
            return taskRepository.findByUuid(uuid);
        }
    }
}
