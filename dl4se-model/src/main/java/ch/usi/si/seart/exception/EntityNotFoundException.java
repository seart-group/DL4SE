package ch.usi.si.seart.exception;

import lombok.AccessLevel;
import lombok.experimental.StandardException;

import javax.persistence.metamodel.Attribute;

@StandardException(access = AccessLevel.PRIVATE)
public abstract class EntityNotFoundException extends RuntimeException {

    private static final String TEMPLATE = "Could not find %s with: [%s = %s]";

    private static String formatMessage(String entity, String field, Object value) {
        return String.format(TEMPLATE, entity, field, value);
    }

    protected <E, T> EntityNotFoundException(Class<E> entity, Attribute<E, T> attribute, T value) {
        super(formatMessage(entity.getSimpleName(), attribute.getName(), value));
    }
}
