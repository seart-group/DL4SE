package usi.si.seart.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.util.ErrorHandler;
import usi.si.seart.exception.TaskFailedException;
import usi.si.seart.model.task.Task;
import usi.si.seart.scheduling.RepoMaintainer;
import usi.si.seart.scheduling.TaskCleaner;
import usi.si.seart.scheduling.TaskRunner;
import usi.si.seart.scheduling.ViewMaintainer;
import usi.si.seart.service.ConfigurationService;
import usi.si.seart.service.EmailService;
import usi.si.seart.service.FileSystemService;
import usi.si.seart.service.TaskService;

import java.time.Clock;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

@Configuration
@EnableScheduling
@DependsOn({
        "ConfigurationInitializingBean",
        "DirectoryInitializationBean",
        "TaskRunnerRecoveryBean",
})
public class SchedulerConfig {

    @Bean(destroyMethod="shutdown")
    public ThreadPoolTaskScheduler taskScheduler(
            ApplicationContext applicationContext,
            ErrorHandler errorHandler,
            TaskCleaner taskCleaner,
            RepoMaintainer repoMaintainer,
            ViewMaintainer viewMaintainer,
            @Value("${scheduling.task.task-cleaner.cron}") CronTrigger taskCleanerTrigger,
            @Value("${scheduling.task.repo-maintainer.cron}") CronTrigger repoMaintainerTrigger,
            @Value("${scheduling.task.view-maintainer.cron}") CronTrigger viewMaintainerTrigger,
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

        threadPoolTaskScheduler.schedule(taskCleaner, taskCleanerTrigger);
        threadPoolTaskScheduler.schedule(repoMaintainer, repoMaintainerTrigger);
        threadPoolTaskScheduler.schedule(viewMaintainer, viewMaintainerTrigger);

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
                fileSystemService.cleanTaskFiles(task);
                emailService.sendTaskNotificationEmail(task);
            }
        };
    }
}
