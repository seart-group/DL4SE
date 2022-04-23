package usi.si.seart.config;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.util.ErrorHandler;
import usi.si.seart.exception.TaskFailedException;
import usi.si.seart.model.task.Status;
import usi.si.seart.model.task.Task;
import usi.si.seart.service.CodeService;
import usi.si.seart.service.FileSystemService;
import usi.si.seart.service.TaskService;
import usi.si.seart.task.TaskCleaner;
import usi.si.seart.task.TaskRunner;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.ZoneOffset;

@Slf4j
@Configuration
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class SchedulerConfig {

    JsonMapper jsonMapper;

    CodeService codeService;
    TaskService taskService;
    FileSystemService fileSystemService;
    ConversionService conversionService;

    PlatformTransactionManager transactionManager;

    @PersistenceContext
    EntityManager entityManager;

    @NonFinal
    @Value("${spring.jpa.properties.hibernate.jdbc.fetch_size}")
    Integer fetchSize;

    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler threadPoolTaskScheduler = new ThreadPoolTaskScheduler();
        threadPoolTaskScheduler.setPoolSize(2);
        threadPoolTaskScheduler.setThreadNamePrefix("DL4SEScheduler");
        threadPoolTaskScheduler.setErrorHandler(new SchedulerErrorHandler());
        threadPoolTaskScheduler.initialize();
        threadPoolTaskScheduler.schedule(getTaskCleaner(), new CronTrigger("@hourly", ZoneOffset.UTC));
        threadPoolTaskScheduler.scheduleWithFixedDelay(getTaskRunner(), 1000);
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
                fileSystemService,
                conversionService,
                transactionManager,
                entityManager,
                fetchSize
        );
    }

    private class SchedulerErrorHandler implements ErrorHandler {

        @Override
        public void handleError(Throwable t) {
            if (t instanceof TaskFailedException) {
                handleError((TaskFailedException) t);
            } else {
                log.error("Unhandled exception occurred while performing a scheduled job.", t);
            }
        }

        private void handleError(TaskFailedException ex) {
            Task task = ex.getTask();
            task.setStatus(Status.ERROR);
            task.setExpired(true);
            taskService.update(task);
            log.error(ex.getMessage(), ex.getCause());
        }
    }
}
