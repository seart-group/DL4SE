package ch.usi.si.seart.crawler.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.task.TaskSchedulerCustomizer;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

@Configuration
@EnableScheduling
public class SchedulerConfig implements TaskSchedulerCustomizer {

    @Override
    public void customize(ThreadPoolTaskScheduler taskScheduler) {
        taskScheduler.setErrorHandler(new SchedulerErrorHandler());
    }

    @Slf4j
    private static class SchedulerErrorHandler implements ErrorHandler {

        @Override
        public void handleError(Throwable throwable) {
            log.error("Unhandled exception occurred while performing a scheduled job.", throwable);
        }
    }
}
