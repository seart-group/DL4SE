package ch.usi.si.seart.server.scheduling;

import ch.usi.si.seart.model.code.Code;
import ch.usi.si.seart.model.job.Job;
import ch.usi.si.seart.model.task.Status;
import ch.usi.si.seart.model.task.Task;
import ch.usi.si.seart.server.exception.TaskFailedException;
import ch.usi.si.seart.server.service.CodeService;
import ch.usi.si.seart.server.service.EmailService;
import ch.usi.si.seart.server.service.FileSystemService;
import ch.usi.si.seart.server.service.TaskService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.Iterable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.zip.GZIPOutputStream;

@Slf4j
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TaskRunner implements Runnable {

    JsonMapper jsonMapper;

    CodeService codeService;
    TaskService taskService;
    EmailService emailService;
    FileSystemService fileSystemService;
    ConversionService conversionService;

    PlatformTransactionManager transactionManager;

    @PersistenceContext
    EntityManager entityManager;

    Integer fetchSize;

    @Autowired
    public TaskRunner(
            JsonMapper jsonMapper,
            CodeService codeService,
            TaskService taskService,
            EmailService emailService,
            FileSystemService fileSystemService,
            ConversionService conversionService,
            PlatformTransactionManager transactionManager,
            EntityManager entityManager,
            @Value("${spring.jpa.properties.hibernate.jdbc.fetch_size}") Integer fetchSize
    ) {
        this.jsonMapper = jsonMapper;
        this.codeService = codeService;
        this.taskService = taskService;
        this.emailService = emailService;
        this.fileSystemService = fileSystemService;
        this.conversionService = conversionService;
        this.transactionManager = transactionManager;
        this.entityManager = entityManager;
        this.fetchSize = fetchSize;
    }

    @Override
    public void run() {
        log.debug("Fetching next task to execute...");
        Optional<Task> next;
        synchronized (TaskRunner.class) {
            next = taskService.getNext();
        }
        next.ifPresentOrElse(this::run, () -> log.debug("No tasks to execute!"));
    }

    @SuppressWarnings("unchecked")
    private void run(Task task) {
        log.info("Executing task: [{}]", task.getUuid());
        Job dataset = task.getDataset();
        // FIXME: 06.10.2023 This will need to be changed later for other dataset types
        if (!Job.CODE.equals(dataset)) {
            String message = "Unsupported task type: " + dataset;
            UnsupportedOperationException cause = new UnsupportedOperationException(message);
            throw new TaskFailedException(task, cause);
        }
        Specification<Code> specification = conversionService.convert(task, Specification.class);
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        transactionTemplate.setReadOnly(true);
        transactionTemplate.execute(new TransactionCallback(task, specification));
    }

    @FieldDefaults(level = AccessLevel.PRIVATE)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    private class TransactionCallback extends TransactionCallbackWithoutResult {

        Task task;

        Specification<Code> specification;

        @Override
        protected void doInTransactionWithoutResult(TransactionStatus status) {
            try {
                JsonNode processing = task.getProcessing();
                Long totalResults = codeService.count(specification);
                task.setTotalResults(totalResults);
                task = taskService.update(task);
                Path exportPath = fileSystemService.createTaskArchive(task);
                try (
                        Stream<Code> stream = codeService.streamAllDynamically(specification);
                        FileOutputStream fileOutputStream = new FileOutputStream(exportPath.toFile(), true);
                        GZIPOutputStream gzipOutputStream = new GZIPOutputStream(fileOutputStream);
                        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(gzipOutputStream);
                        BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter)
                ) {
                    Iterable<Code> iterable = stream::iterator;
                    long count = task.getProcessedResults();
                    for (Code code: iterable) {
                        count += 1;
                        Long id = code.getId();
                        task.setCheckpointId(id);
                        task.setProcessedResults(count);

                        // TODO: 06.10.2023 Apply transformations here

                        ObjectNode json = jsonMapper.valueToTree(code);
                        json.remove("id");
                        Optional.ofNullable(processing.get("include_ast"))
                                .map(JsonNode::asBoolean)
                                .filter(Boolean::booleanValue)
                                .ifPresentOrElse(value -> {}, () -> json.remove("ast"));
                        Optional.ofNullable(processing.get("include_symbolic_expression"))
                                .map(JsonNode::asBoolean)
                                .filter(Boolean::booleanValue)
                                .ifPresentOrElse(value -> {}, () -> json.remove("symbolic_expression"));

                        String serialized = jsonMapper.writeValueAsString(json);
                        bufferedWriter.write(serialized);
                        bufferedWriter.newLine();

                        if (count % fetchSize == 0) {
                            Long fileSize = fileSystemService.getFileSize(task);
                            task.setSize(fileSize);
                            task = taskService.update(task);
                            bufferedWriter.flush();
                            entityManager.clear();
                        }
                    }
                }

                Long fileSize = fileSystemService.getFileSize(task);
                task.setSize(fileSize);
                task.setStatus(Status.FINISHED);
                task.setFinished(LocalDateTime.now(ZoneOffset.UTC));
                task = taskService.update(task);
                log.info("Finished task:  [{}]", task.getUuid());
                emailService.sendTaskNotificationEmail(task);
            } catch (OptimisticLockingFailureException ex) {
                /*
                 * Only two threads can modify a single task:
                 *
                 *   1) The task executor
                 *   2) The /cancel route
                 *
                 * That means if the locking failure occurs here,
                 * the task has been marked as cancelled,
                 * and we should move on to executing the next task.
                 * Still, this exception should be logged at the debug level,
                 * just in case we run into other locking
                 * issues when we introduce new features...
                */
                fileSystemService.cleanTaskFiles(task);
                log.info("Cancelled task: [{}]", task.getUuid());
                log.debug("", ex);
            } catch (Throwable thr) {
                throw new TaskFailedException(task, thr);
            }
        }
    }
}
