package usi.si.seart.task;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import usi.si.seart.model.task.Task;
import usi.si.seart.service.FileSystemService;
import usi.si.seart.service.TaskService;

import java.util.function.Consumer;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskCleaner implements Runnable {

    TaskService taskService;
    FileSystemService fileSystemService;

    @Override
    public void run() {
        log.debug("Cleaning up expired task files...");
        Consumer<Task> deleteAndMark = task -> {
            log.trace("Deleting export file for task: [{}]", task.getUuid());
            task.setExpired(true);
            fileSystemService.deleteExportFile(task);
        };
        taskService.forEachNonExpired(deleteAndMark);
        log.debug("Cleaning complete!");
    }
}
