package ch.usi.si.seart.server.config;

import ch.usi.si.seart.model.task.Task;
import ch.usi.si.seart.server.config.properties.SchedulingProperties;
import ch.usi.si.seart.server.exception.TaskFailedException;
import ch.usi.si.seart.server.scheduling.RepoMaintainer;
import ch.usi.si.seart.server.scheduling.TaskCleaner;
import ch.usi.si.seart.server.scheduling.TaskRunner;
import ch.usi.si.seart.server.scheduling.ViewMaintainer;
import ch.usi.si.seart.server.service.ConfigurationService;
import ch.usi.si.seart.server.service.EmailService;
import ch.usi.si.seart.server.service.FileSystemService;
import ch.usi.si.seart.server.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.util.ErrorHandler;

import java.time.Clock;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

@Configuration
@EnableScheduling
@DependsOn({
        "ConfigurationInitializingBean",
        "directoryInitializationBean",
})
public class SchedulerConfig {

    @Bean(destroyMethod="shutdown")
    public ThreadPoolTaskScheduler taskScheduler(
            ApplicationContext applicationContext,
            ErrorHandler errorHandler,
            TaskCleaner taskCleaner,
            RepoMaintainer repoMaintainer,
            ViewMaintainer viewMaintainer,
            SchedulingProperties properties,
            ConfigurationService configurationService
    ) {
        AutowireCapableBeanFactory autowireCapableBeanFactory = applicationContext.getAutowireCapableBeanFactory();
        Integer runners = configurationService.get("task_runner_count", Integer.class);
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler() {

            private final Set<ScheduledFuture<?>> futures = new HashSet<>();

            @Override
            public ScheduledFuture<?> scheduleWithFixedDelay(Runnable task, long delay) {
                ScheduledFuture<?> future = super.scheduleWithFixedDelay(task, delay);
                futures.add(future);
                return future;
            }

            @Override
            public ScheduledFuture<?> schedule(Runnable task, Trigger trigger) {
                ScheduledFuture<?> future =  super.schedule(task, trigger);
                futures.add(future);
                return future;
            }

            @Override
            public void shutdown() {
                futures.forEach(future -> future.cancel(true));
                super.shutdown();
            }
        };
        threadPoolTaskScheduler.setDaemon(true);
        threadPoolTaskScheduler.setClock(Clock.systemUTC());
        threadPoolTaskScheduler.setPoolSize(3 + runners);
        threadPoolTaskScheduler.setThreadNamePrefix("scheduling-");
        threadPoolTaskScheduler.setErrorHandler(errorHandler);
        threadPoolTaskScheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        threadPoolTaskScheduler.initialize();

        threadPoolTaskScheduler.schedule(taskCleaner, properties.getTaskCleanerCron());
        threadPoolTaskScheduler.schedule(repoMaintainer, properties.getRepoMaintainerCron());
        threadPoolTaskScheduler.schedule(viewMaintainer, properties.getViewMaintainerCron());

        for (int i = 0; i < runners; i++) {
            TaskRunner taskRunner = autowireCapableBeanFactory.createBean(TaskRunner.class);
            threadPoolTaskScheduler.scheduleWithFixedDelay(taskRunner, 500);
        }

        return threadPoolTaskScheduler;
    }

    @Bean
    public ErrorHandler errorHandler(
            TaskService taskService, EmailService emailService, FileSystemService fileSystemService
    ) {
        return new ErrorHandler() {

            private final Logger log = LoggerFactory.getLogger(
                    SchedulerConfig.class.getCanonicalName() + "$" + ErrorHandler.class.getSimpleName()
            );

            @Override
            public void handleError(Throwable t) {
                if (t instanceof TaskFailedException) {
                    handleError((TaskFailedException) t);
                } else {
                    log.error("Unhandled exception occurred while performing a scheduled job.", t);
                }
            }

            private void handleError(TaskFailedException ex) {
                log.warn(ex.getMessage());
                taskService.registerException(ex);
                Task task = ex.getTask();
                fileSystemService.cleanArchive(task);
                emailService.sendTaskNotificationEmail(task);
            }
        };
    }
}
