package usi.si.seart.task;

import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.AccessLevel;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
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
import usi.si.seart.service.FileSystemService;
import usi.si.seart.service.TaskService;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

@Slf4j
@Component
@EnableScheduling
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class TaskExecutor {

    JsonMapper jsonMapper;

    CodeService codeService;
    TaskService taskService;
    FileSystemService fileSystemService;
    ConversionService conversionService;

    @PersistenceContext
    EntityManager entityManager;

    @NonFinal
    @Value("${spring.jpa.properties.hibernate.jdbc.fetch_size}")
    Integer fetchSize;

    @Scheduled(fixedDelay = 1, timeUnit = TimeUnit.SECONDS)
    @Transactional(readOnly = true)
    public void run() {
        log.debug("Fetching next task to execute...");
        Optional<Task> optional = taskService.getNext();
        optional.ifPresentOrElse(this::run, () -> log.debug("No tasks to execute!"));
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
        log.info("Finished task:  [{}]", task.getUuid());
        // TODO 17.04.22: Send email notification containing dataset link
    }

    private void run(CodeTask task) {
        org.jooq.Query query = conversionService.convert(task, org.jooq.Query.class);
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

        task.setStatus(Status.EXECUTING);
        task.setStarted(LocalDateTime.now(ZoneOffset.UTC));
        taskService.update(task);

        Long totalResults = codeService.countTotalResults(query, codeClass);
        task.setTotalResults(totalResults);
        taskService.update(task);

        try {
            Path exportPath = fileSystemService.getExportPath(task);
            @Cleanup BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(
                            new FileOutputStream(exportPath.toFile(), true),
                            StandardCharsets.UTF_8
                    )
            );
            Consumer<Code> codeConsumer = generateCodeConsumer(task, writer);
            @Cleanup Stream<? extends Code> stream = codeService.createPipeline(query, pipeline, codeClass);
            stream.forEach(codeConsumer);
        } catch (IOException ex) {
            throw new TaskFailedException(task, ex);
        }

        task.setStatus(Status.FINISHED);
        task.setFinished(LocalDateTime.now(ZoneOffset.UTC));
        taskService.update(task);
    }

    private Consumer<Code> generateCodeConsumer(CodeTask task, BufferedWriter writer) {
        AtomicLong counter = new AtomicLong(task.getProcessedResults());
        return code -> {
            entityManager.detach(code);

            long id = code.getId();
            long count = counter.incrementAndGet();
            task.setCheckpointId(id);
            task.setProcessedResults(count);

            try {
                String serialized = jsonMapper.writeValueAsString(code);
                writer.write(serialized);
                writer.newLine();

                if (count % fetchSize == 0) {
                    taskService.update(task);
                    writer.flush();
                    entityManager.clear();
                }
            } catch (IOException ex) {
                throw new TaskFailedException(task, ex);
            }
        };
    }
}
