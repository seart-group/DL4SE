package usi.si.seart.exception;

public class TokenNotFoundException extends EntityNotFoundException {

    private static final String format = "Could not find Token with: [%s = %s]";

    public TokenNotFoundException(String field, Object value) {
        super(String.format(format, field, value.toString()));
    }
}
