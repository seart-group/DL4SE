package ch.usi.si.seart.crawler.config;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

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

    @Slf4j
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class FixedDelayLoggingRunnable implements Runnable {

        Runnable delegate;

        long delay;

        @Override
        public void run() {
            delegate.run();
            LocalDateTime nextRun = LocalDateTime.now()
                    .plus(delay, ChronoUnit.MILLIS)
                    .truncatedTo(ChronoUnit.SECONDS);
            log.info("Finished! Next run scheduled for: {}", nextRun);
        }

        @Override
        public String toString() {
            return getClass().getSimpleName() + " for " + delegate;
        }
    }
}
