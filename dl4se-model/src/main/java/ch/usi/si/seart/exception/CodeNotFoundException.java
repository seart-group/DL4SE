package ch.usi.si.seart.exception;

import ch.usi.si.seart.model.code.Code;

import javax.persistence.metamodel.Attribute;

public class CodeNotFoundException extends EntityNotFoundException {

    public <T> CodeNotFoundException(Attribute<Code, T> attribute, T value) {
        super(Code.class, attribute, value);
    }
}
