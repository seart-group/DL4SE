package usi.si.seart.exception;

import usi.si.seart.model.task.Task;

import javax.persistence.metamodel.Attribute;

public class TaskNotFoundException extends EntityNotFoundException {

    public <T> TaskNotFoundException(Attribute<Task, T> attribute, T value) {
        super(Task.class, attribute, value);
    }
}
