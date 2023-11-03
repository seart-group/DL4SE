package ch.usi.si.seart.crawler.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

import java.time.Clock;

@Configuration
@EnableScheduling
public class SchedulerConfig {

    @Bean
    public TaskScheduler taskScheduler(ErrorHandler errorHandler) {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("scheduling-");
        taskScheduler.setPoolSize(1);
        taskScheduler.setClock(Clock.systemUTC());
        taskScheduler.setErrorHandler(errorHandler);
        taskScheduler.initialize();
        return taskScheduler;
    }

    @Bean
    public ErrorHandler errorHandler() {
        return new ErrorHandler() {

            private final Logger log = LoggerFactory.getLogger(
                    SchedulerConfig.class.getCanonicalName() + "$" + ErrorHandler.class.getSimpleName()
            );

            @Override
            public void handleError(Throwable t) {
                log.error("Unhandled exception occurred while performing a scheduled job.", t);
            }
        };
    }
}
