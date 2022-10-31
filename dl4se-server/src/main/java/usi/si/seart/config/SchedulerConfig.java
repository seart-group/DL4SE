package usi.si.seart.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ErrorHandler;
import usi.si.seart.exception.TaskFailedException;
import usi.si.seart.model.task.Task;
import usi.si.seart.scheduling.RepoMaintainer;
import usi.si.seart.scheduling.TaskCleaner;
import usi.si.seart.scheduling.TaskRunner;
import usi.si.seart.scheduling.ViewMaintainer;
import usi.si.seart.service.CodeService;
import usi.si.seart.service.ConfigurationService;
import usi.si.seart.service.EmailService;
import usi.si.seart.service.FileSystemService;
import usi.si.seart.service.GitRepoService;
import usi.si.seart.service.TaskService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Clock;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

@Configuration
@DependsOn({"TaskRunnerRecoveryBean", "DirectoryInitializationBean"})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SchedulerConfig {

    JsonMapper jsonMapper;

    CodeService codeService;
    TaskService taskService;
    GitRepoService gitRepoService;
    EmailService emailService;
    FileSystemService fileSystemService;
    ConversionService conversionService;
    ConfigurationService configurationService;

    PlatformTransactionManager transactionManager;

    @PersistenceContext
    EntityManager entityManager;

    @NonFinal
    @Value("${spring.jpa.properties.hibernate.jdbc.fetch_size}")
    Integer fetchSize;

    @Bean(destroyMethod="shutdown")
    public ThreadPoolTaskScheduler taskScheduler() {
        Integer runners = configurationService.get("task_runner_count", Integer.class);
        String taskCleanerCron = configurationService.get("task_cleaner_cron", String.class);
        String repoMaintainerCron = configurationService.get("repo_maintainer_cron", String.class);
        String viewMaintainerCron = configurationService.get("view_maintainer_cron", String.class);

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
        threadPoolTaskScheduler.setThreadNamePrefix("DL4SEScheduler");
        threadPoolTaskScheduler.setErrorHandler(new SchedulerErrorHandler());
        threadPoolTaskScheduler.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        threadPoolTaskScheduler.initialize();

        threadPoolTaskScheduler.schedule(getRepoMaintainer(), new CronTrigger(repoMaintainerCron));
        threadPoolTaskScheduler.schedule(getTaskCleaner(), new CronTrigger(taskCleanerCron));
        threadPoolTaskScheduler.schedule(getViewMaintainer(), new CronTrigger(viewMaintainerCron));
        for (int i = 0; i < runners; i++)
            threadPoolTaskScheduler.scheduleWithFixedDelay(getTaskRunner(), 500);

        return threadPoolTaskScheduler;
    }

    private Runnable getRepoMaintainer() {
        return new RepoMaintainer(gitRepoService);
    }

    private Runnable getTaskCleaner() {
        return new TaskCleaner(taskService, fileSystemService);
    }

    private Runnable getViewMaintainer() {
        return new ViewMaintainer(entityManager, transactionManager);
    }

    private Runnable getTaskRunner() {
        return new TaskRunner(
                jsonMapper,
                codeService,
                taskService,
                emailService,
                fileSystemService,
                conversionService,
                transactionManager,
                entityManager,
                fetchSize
        );
    }

    private class SchedulerErrorHandler implements ErrorHandler {

        private final Logger log = LoggerFactory.getLogger(this.getClass());

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
    }
}
