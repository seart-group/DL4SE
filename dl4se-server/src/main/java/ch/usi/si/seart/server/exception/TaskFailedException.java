package ch.usi.si.seart.server.exception;

import ch.usi.si.seart.model.task.Task;
import lombok.Getter;

import javax.validation.constraints.NotNull;

/**
 * Unchecked exception used to wrap any type of exception that is thrown during task execution. The idea behind this is
 * to propagate the failed task, along with the cause exceptions, to the scheduler's error handler. In this way, we can
 * use the handler to mark the task as failed in the database, and prevent the erroneous task from looping.
 *
 * @author dabico
 */
public class TaskFailedException extends RuntimeException {

    private static final String format = "%s occurred while executing task [%s]";

    @Getter
    private final transient Task task;

    public TaskFailedException(@NotNull Task task, @NotNull Throwable cause) {
        super(String.format(format, cause.getClass().getSimpleName(), task.getUuid()), cause);
        this.task = task;
    }
}
