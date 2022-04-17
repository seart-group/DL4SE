package usi.si.seart.config;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
        threadPoolTaskScheduler.setPoolSize(1);
        threadPoolTaskScheduler.setThreadNamePrefix("DL4SEScheduler");
        threadPoolTaskScheduler.setErrorHandler(new SchedulerErrorHandler());
        return threadPoolTaskScheduler;
    }

    private class SchedulerErrorHandler implements ErrorHandler {

        @Override
        public void handleError(Throwable t) {
            if (t instanceof TaskFailedException) {
                handleError((TaskFailedException) t);
            } else {
                log.error("An unexpected exception occurred while performing a scheduled job.", t);
            }
        }

        private void handleError(TaskFailedException ex) {
            Task failedTask = ex.getTask();
            failedTask.setStatus(Status.ERROR);
            taskService.update(failedTask);
            log.error(ex.getMessage(), ex.getCause());
        }
    }
}
