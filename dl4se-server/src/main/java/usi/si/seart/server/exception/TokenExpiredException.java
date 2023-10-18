package usi.si.seart.server.exception;

import usi.si.seart.model.user.token.Token;

public class TokenExpiredException extends RuntimeException {

    public TokenExpiredException(Token token) {
        super(
                String.format("Token [%s] has expired on [%s]", token.getValue(), token.getExpires())
        );
    }
}
