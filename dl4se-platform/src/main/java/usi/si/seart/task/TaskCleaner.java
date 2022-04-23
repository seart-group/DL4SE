package usi.si.seart.task;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import usi.si.seart.model.task.Task;
import usi.si.seart.service.FileSystemService;
import usi.si.seart.service.TaskService;

import java.util.List;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskCleaner implements Runnable {

    TaskService taskService;
    FileSystemService fileSystemService;

    @Override
    public void run() {
        log.info("Cleaning up expired task files...");
        List<Task> tasks = taskService.getTasksForCleanup();
        tasks.forEach(this::logAndDeleteFile);
        log.info("Cleaning complete!");
    }

    private void logAndDeleteFile(Task task) {
        log.debug("Deleting files: [{}]", task.getUuid());
        fileSystemService.deleteExportFile(task);
    }
}
