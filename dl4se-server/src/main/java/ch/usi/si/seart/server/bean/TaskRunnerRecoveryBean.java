package ch.usi.si.seart.server.bean;

import ch.usi.si.seart.model.task.Status;
import ch.usi.si.seart.server.service.StatisticsService;
import ch.usi.si.seart.server.service.TaskService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component("TaskRunnerRecoveryBean")
@AllArgsConstructor(onConstructor_ = @Autowired)
public class TaskRunnerRecoveryBean implements InitializingBean {

    private final StatisticsService statisticsService;
    private final TaskService taskService;

    @Override
    public void afterPropertiesSet() {
        Long executingCount = statisticsService.countTasksByStatus().get(Status.EXECUTING);
        if (executingCount > 0) {
            log.info("Returning {} interrupted tasks back to queue...", executingCount);
            taskService.forEachExecuting(task -> task.setStatus(Status.QUEUED));
        }
    }
}
