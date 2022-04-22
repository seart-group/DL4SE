package usi.si.seart.service;

import lombok.AccessLevel;
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
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public interface TaskService {

    boolean canCreateTask(User user);
    boolean activeTaskExists(User user, Query query, Processing processing);
    void create(User requester, LocalDateTime requestedAt, CodeQuery query, CodeProcessing processing);
    <T extends Task> T update(T task);
    Optional<Task> getNext();
    Optional<Task> getWithUUID(UUID uuid);
    List<Task> getTasksForCleanup();

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

        @Override
        public List<Task> getTasksForCleanup() {
            LocalDateTime currentHour = LocalDateTime.now(ZoneOffset.UTC).truncatedTo(ChronoUnit.HOURS);
            LocalDateTime oneWeekAgoUpper = currentHour.minusWeeks(1);
            LocalDateTime oneWeekAgoLower = oneWeekAgoUpper.minusHours(1);
            return taskRepository.findByFinishedBetween(oneWeekAgoLower, oneWeekAgoUpper);
        }
    }
}
