package usi.si.seart.scheduling;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import usi.si.seart.model.task.Task;
import usi.si.seart.service.FileSystemService;
import usi.si.seart.service.TaskService;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskCleaner implements Runnable {

    TaskService taskService;
    FileSystemService fileSystemService;

    @Override
    public void run() {
        log.debug("Cleaning up expired task files...");
        taskService.forEachNonExpired(this::cleanAndFlag);
        log.debug("Task cleaning complete!");
    }

    private void cleanAndFlag(Task task) {
        log.trace("Deleting export file for task: [{}]", task.getUuid());
        task.setExpired(true);
        fileSystemService.cleanTaskFiles(task);
    }
}
