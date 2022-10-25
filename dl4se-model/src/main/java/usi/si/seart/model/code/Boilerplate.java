package usi.si.seart.model.code;

import java.util.Optional;

public enum Boilerplate {
    CONSTRUCTOR, GETTER, SETTER, TO_STRING, EQUALS, HASH_CODE, BUILDER;

    public static Boilerplate valueOfNullable(Object value) {
        return Optional.ofNullable(value)
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .map(Boilerplate::valueOf)
                .orElse(null);
    }
}