package usi.si.seart.task;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import usi.si.seart.model.task.Task;
import usi.si.seart.service.FileSystemService;
import usi.si.seart.service.TaskService;

import java.util.List;

@Slf4j
@Component
@EnableScheduling
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskResultCleaner {

    TaskService taskService;
    FileSystemService fileSystemService;

    @Scheduled(cron = "@hourly")
    public void run() {
        log.debug("Cleaning up expired task files...");
        List<Task> tasks = taskService.getTasksForCleanup();
        tasks.forEach(this::logAndDeleteFile);
        log.debug("Done!");
    }

    private void logAndDeleteFile(Task task) {
        log.debug("Deleting files: [{}]", task.getUuid());
        fileSystemService.deleteExportFile(task);
    }
}
