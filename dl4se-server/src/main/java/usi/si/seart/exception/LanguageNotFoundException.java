package usi.si.seart.exception;

public class LanguageNotFoundException extends EntityNotFoundException {

    private static final String format = "Could not find Language with: [%s = %s]";

    public LanguageNotFoundException(String field, Object value) {
        super(String.format(format, field, value.toString()));
    }
}
