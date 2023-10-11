package usi.si.seart.bean;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import usi.si.seart.model.task.Status;
import usi.si.seart.service.StatisticsService;
import usi.si.seart.service.TaskService;

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
