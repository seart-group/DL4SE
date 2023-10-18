package usi.si.seart.server.exception;

import usi.si.seart.model.user.token.Token;

import javax.persistence.metamodel.Attribute;

public class TokenNotFoundException extends EntityNotFoundException {

    public <T> TokenNotFoundException(Attribute<Token, T> attribute, T value) {
        super(Token.class, attribute, value);
    }
}
