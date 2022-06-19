package usi.si.seart.task;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.jooq.Queries;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import usi.si.seart.exception.TaskFailedException;
import usi.si.seart.model.code.Code;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;
import usi.si.seart.model.task.CodeTask;
import usi.si.seart.model.task.Status;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.task.query.FileQuery;
import usi.si.seart.model.task.query.FunctionQuery;
import usi.si.seart.model.task.query.Query;
import usi.si.seart.service.CodeService;
import usi.si.seart.service.EmailService;
import usi.si.seart.service.FileSystemService;
import usi.si.seart.service.TaskService;

import javax.persistence.EntityManager;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@Slf4j
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskRunner implements Runnable {

    JsonMapper jsonMapper;

    CodeService codeService;
    TaskService taskService;
    EmailService emailService;
    FileSystemService fileSystemService;
    ConversionService conversionService;

    PlatformTransactionManager transactionManager;

    EntityManager entityManager;

    Integer fetchSize;

    @Override
    public void run() {
        log.debug("Fetching next task to execute...");
        Optional<Task> next;
        synchronized (TaskRunner.class) {
            next = taskService.getNext();
        }
        next.ifPresentOrElse(this::run, () -> log.debug("No tasks to execute!"));
    }

    private void run(Task task) {
        log.info("Executing task: [{}]", task.getUuid());
        if (task instanceof CodeTask) {
            run((CodeTask) task);
        } else {
            String message = "Unsupported task type: " + task.getClass().getName();
            UnsupportedOperationException cause = new UnsupportedOperationException(message);
            throw new TaskFailedException(task, cause);
        }
    }

    private void run(CodeTask task) {
        @SuppressWarnings("ConstantConditions")
        org.jooq.Query[] queries = conversionService.convert(task, Queries.class).queries();
        // TODO 14.04.22: Convert CodeProcessing to a UnaryOperator
        UnaryOperator<Code> pipeline = UnaryOperator.identity();

        Class<? extends Code> codeClass;
        Query codeQuery = task.getQuery();
        if (codeQuery instanceof FileQuery) {
            codeClass = File.class;
        } else if (codeQuery instanceof FunctionQuery) {
            codeClass = Function.class;
        } else {
            String message = "Unsupported code granularity level: " + codeQuery.getClass().getName();
            UnsupportedOperationException cause = new UnsupportedOperationException(message);
            throw new TaskFailedException(task, cause);
        }

        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setReadOnly(true);
        transactionTemplate.execute(
            new TaskRunnerTransactionCallbackWithoutResult(task, codeClass, queries[0], queries[1], pipeline)
        );
    }


    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private class TaskRunnerTransactionCallbackWithoutResult extends TransactionCallbackWithoutResult {

        @NonFinal Task task;

        Class<? extends Code> codeClass;

        org.jooq.Query resultQuery;
        org.jooq.Query countQuery;

        UnaryOperator<Code> pipeline;

        @Override
        protected void doInTransactionWithoutResult(TransactionStatus status) {
            try {
                Path exportPath = fileSystemService.createTaskFile(task);
                @Cleanup BufferedWriter writer = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(exportPath.toFile(), true),
                                StandardCharsets.UTF_8
                        )
                );

                Long totalResults = codeService.countTotalResults(countQuery);
                task.setTotalResults(totalResults);
                task = taskService.update(task);

                @Cleanup Stream<Code> stream = codeService.createPipeline(resultQuery, pipeline, codeClass);
                Iterable<Code> iterable = stream::iterator;
                long count = task.getProcessedResults();
                for (Code code : iterable) {
                    entityManager.detach(code);
                    count += 1;

                    long id = code.getId();
                    task.setCheckpointId(id);
                    task.setProcessedResults(count);

                    String serialized = jsonMapper.writeValueAsString(code);
                    writer.write(serialized);
                    writer.newLine();

                    if (count % fetchSize == 0) {
                        task = taskService.update(task);
                        writer.flush();
                        entityManager.clear();
                    }
                }

                task = taskService.update(task);

                fileSystemService.compressTaskFile(task);

                task.setStatus(Status.FINISHED);
                task.setFinished(LocalDateTime.now(ZoneOffset.UTC));
                task = taskService.update(task);
                log.info("Finished task:  [{}]", task.getUuid());
                emailService.sendTaskNotificationEmail(task);
            } catch (OptimisticLockingFailureException ex) {
                // Only two threads can modify a single task:
                // 1) The task executor
                // 2) The /cancel route
                // That means if the locking failure occurs here, the task has been marked as cancelled, and we should
                // move on to executing the next task. Still, this exception should be logged at the debug level, just
                // in case we run into other locking issues when we introduce new features...
                fileSystemService.cleanTaskFiles(task);
                log.info("Cancelled task: [{}]", task.getUuid());
                log.debug("", ex);
            } catch (Throwable thr) {
                throw new TaskFailedException(task, thr);
            }
        }
    }
}
