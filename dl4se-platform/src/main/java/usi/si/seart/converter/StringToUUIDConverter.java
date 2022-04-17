package usi.si.seart.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

import java.util.UUID;

public class StringToUUIDConverter implements Converter<String, UUID> {

    @Override
    @NonNull
    public UUID convert(@NonNull String source) {
        return UUID.fromString(source);
    }
}
