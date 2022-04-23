package usi.si.seart.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;
import usi.si.seart.exception.TaskFailedException;
import usi.si.seart.model.task.Status;
import usi.si.seart.model.task.Task;
import usi.si.seart.service.TaskService;

@Slf4j
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SchedulerConfig {

    TaskService taskService;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(2);
        threadPoolTaskScheduler.setThreadNamePrefix("DL4SEScheduler");
        threadPoolTaskScheduler.setErrorHandler(new SchedulerErrorHandler());
        return threadPoolTaskScheduler;
    }

    private class SchedulerErrorHandler implements ErrorHandler {

        @Override
        public void handleError(Throwable t) {
            if (t instanceof TaskFailedException) {
                handleError((TaskFailedException) t);
            } else if (t instanceof OptimisticLockingFailureException) {
                handleError((OptimisticLockingFailureException) t);
            } else {
                log.error("An unexpected exception occurred while performing a scheduled job.", t);
            }
        }

        private void handleError(TaskFailedException ex) {
            Task task = ex.getTask();
            taskService.update(task, task::setStatus, Status.ERROR);
            log.error(ex.getMessage(), ex.getCause());
        }

        private void handleError(OptimisticLockingFailureException ex) {
            // Allowing this exception to reach the handler also means that the task run is definitively cancelled
            // Usually thrown as a result of a general update method being called after a status change (cancellation)
            // TODO 22.04.22: Should we do something else here?
            log.debug("", ex);
        }
    }
}
