package ch.usi.si.seart.exception;

import ch.usi.si.seart.model.Language;

import javax.persistence.metamodel.Attribute;

public class LanguageNotFoundException extends EntityNotFoundException {

    public <T> LanguageNotFoundException(Attribute<Language, T> attribute, T value) {
        super(Language.class, attribute, value);
    }
}
