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
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ErrorHandler;
import usi.si.seart.exception.TaskFailedException;
import usi.si.seart.model.task.Task;
import usi.si.seart.service.CodeService;
import usi.si.seart.service.ConfigurationService;
import usi.si.seart.service.EmailService;
import usi.si.seart.service.FileSystemService;
import usi.si.seart.service.TaskService;
import usi.si.seart.task.TaskCleaner;
import usi.si.seart.task.TaskRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.Clock;

@Configuration
@DependsOn({"TaskRunnerRecoveryBean", "DirectoryInitializationBean"})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SchedulerConfig {

    JsonMapper jsonMapper;

    CodeService codeService;
    TaskService taskService;
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

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        Integer runners = configurationService.get("task_runner_count", Integer.class);
        String cron = configurationService.get("task_cleaner_cron", String.class);

        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setClock(Clock.systemUTC());
        threadPoolTaskScheduler.setPoolSize(1 + runners);
        threadPoolTaskScheduler.setThreadNamePrefix("DL4SEScheduler");
        threadPoolTaskScheduler.setErrorHandler(new SchedulerErrorHandler());
        threadPoolTaskScheduler.initialize();

        threadPoolTaskScheduler.schedule(getTaskCleaner(), new CronTrigger(cron));
        for (int i = 0; i < runners; i++)
            threadPoolTaskScheduler.scheduleWithFixedDelay(getTaskRunner(), 500);

        return threadPoolTaskScheduler;
    }

    private Runnable getTaskCleaner() {
        return new TaskCleaner(taskService, fileSystemService);
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
