package ch.usi.si.seart.server.converter;

import ch.usi.si.seart.model.dataset.Dataset;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class StringToDatasetConverter implements Converter<String, Dataset> {

    @Override
    @NonNull
    public Dataset convert(@NonNull String source) {
        return Dataset.valueOf(source.toUpperCase());
    }
}
