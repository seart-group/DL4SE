package ch.usi.si.seart.exception;

import ch.usi.si.seart.model.Configuration;

import javax.persistence.metamodel.Attribute;

public class ConfigurationNotFoundException extends EntityNotFoundException {

    public <T> ConfigurationNotFoundException(Attribute<Configuration, T> attribute, T value) {
        super(Configuration.class, attribute, value);
    }
}
