package usi.si.seart.model.code;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Getter
@ToString
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public enum Boilerplate {
    CONSTRUCTOR("constructor"),
    GETTER("getter"),
    SETTER("setter"),
    TO_STRING("toString"),
    EQUALS("equals"),
    HASH_CODE("hashCode"),
    BUILDER("builder");

    String value;
}
