package usi.si.seart.exception;

public class ConfigurationNotFoundException extends EntityNotFoundException {

    private static final String format = "Could not find Configuration with key: %s";

    public ConfigurationNotFoundException(String key) {
        super(String.format(format, key));
    }
}
