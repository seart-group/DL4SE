package usi.si.seart.server.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import usi.si.seart.model.Configuration;
import usi.si.seart.server.dto.ConfigurationDto;

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
