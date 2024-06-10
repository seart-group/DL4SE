package ch.usi.si.seart.server.scheduling;

import ch.usi.si.seart.model.task.Task;
import ch.usi.si.seart.server.service.FileSystemService;
import ch.usi.si.seart.server.service.TaskService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskCleaner implements Runnable {

    TaskService taskService;
    FileSystemService fileSystemService;

    @Override
    public void run() {
        log.info("Cleaning up expired task files...");
        taskService.forEachNonExpired(this::run);
        log.info("Task cleaning complete!");
    }

    private void run(Task task) {
        log.debug("Deleting export file for task: [{}]", task.getUuid());
        task.setExpired(true);
        fileSystemService.cleanArchive(task);
    }
}
