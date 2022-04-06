package usi.si.seart.exception;

public class UserNotFoundException extends EntityNotFoundException {

    private static final String format = "Could not find User with: [%s = %s]";

    public UserNotFoundException(String field, Object value) {
        super(String.format(format, field, value.toString()));
    }
}
