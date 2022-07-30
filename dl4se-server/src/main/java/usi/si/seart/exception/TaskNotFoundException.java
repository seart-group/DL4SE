package usi.si.seart.exception;

public class TaskNotFoundException extends EntityNotFoundException {

    private static final String format = "Could not find Task with: [%s = %s]";

    public TaskNotFoundException(String field, Object value) {
        super(String.format(format, field, value.toString()));
    }
}
