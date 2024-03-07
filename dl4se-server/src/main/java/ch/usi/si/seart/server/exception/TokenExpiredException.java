package ch.usi.si.seart.server.exception;

import ch.usi.si.seart.model.user.token.Token;

public class TokenExpiredException extends RuntimeException {

    private static final String TEMPLATE = "Token [%s] has expired on [%s]";

    public TokenExpiredException(Token token) {
        super(String.format(TEMPLATE, token.getValue(), token.getExpires()));
    }
}
