package ch.usi.si.seart.server.converter;

import ch.usi.si.seart.model.Configuration;
import ch.usi.si.seart.server.dto.ConfigurationDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class DtoToConfigurationConverter implements Converter<ConfigurationDto, Configuration> {

    @Override
    @NonNull
    public Configuration convert(@NonNull ConfigurationDto source) {
        return Configuration.builder()
                .key(source.getKey())
                .value(source.getValue())
                .build();
    }
}
