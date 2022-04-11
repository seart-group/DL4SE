package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import usi.si.seart.model.task.CodeTask;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.task.processing.CodeProcessing;
import usi.si.seart.model.task.processing.Processing;
import usi.si.seart.model.task.query.CodeQuery;
import usi.si.seart.model.task.query.Query;
import usi.si.seart.model.user.User;
import usi.si.seart.repository.TaskRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public interface TaskService {

    boolean canCreateTask(User user);
    boolean activeTaskExists(User user, Query query, Processing processing);
    void create(User requester, LocalDateTime requestedAt, CodeQuery query, CodeProcessing processing);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    class TaskServiceImpl implements TaskService {

        Path fileStorageDirPath;
        TaskRepository taskRepository;

        @NonFinal
        @Value("${app.request.limit}")
        Long userTaskLimit;

        @Override
        public boolean canCreateTask(User user) {
            Long activeTasks = taskRepository.countActiveByUser(user);
            return activeTasks < userTaskLimit;
        }

        @Override
        public boolean activeTaskExists(User user, Query query, Processing processing) {
            int taskHash = Objects.hash(user, query, processing);
            return taskRepository.findActiveByUser(user).stream()
                    .mapToInt(Task::hashCode)
                    .anyMatch(hash -> hash == taskHash);
        }

        @Override
        @SneakyThrows({IOException.class})
        public void create(User requester, LocalDateTime requestedAt, CodeQuery query, CodeProcessing processing) {
            UUID uuid = UUID.randomUUID();
            String prefix = requester.getId() + "_" + uuid + "_";
            Path requestFile = Files.createTempFile(fileStorageDirPath, prefix, ".jsonl");
            Task task = CodeTask.builder()
                    .uuid(uuid)
                    .user(requester)
                    .submitted(requestedAt)
                    .exportPath(requestFile.toString())
                    .query(query)
                    .processing(processing)
                    .build();

            query.setTask(task);
            processing.setTask(task);
            taskRepository.save(task);
        }
    }
}
