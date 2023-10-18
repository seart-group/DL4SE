package usi.si.seart.server.exception;

import usi.si.seart.model.Configuration;

import javax.persistence.metamodel.Attribute;

public class ConfigurationNotFoundException extends EntityNotFoundException {

    public <T> ConfigurationNotFoundException(Attribute<Configuration, T> attribute, T value) {
        super(Configuration.class, attribute, value);
    }
}
