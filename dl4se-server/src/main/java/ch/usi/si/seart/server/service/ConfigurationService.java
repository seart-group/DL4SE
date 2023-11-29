package ch.usi.si.seart.server.service;

import ch.usi.si.seart.model.Configuration;
import ch.usi.si.seart.repository.ConfigurationRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

public interface ConfigurationService {

    PropertySource<?> getPropertySource();
    <T> T get(String key, Class<T> type);
    Map<String, String> get();
    Map<String, String> update(Collection<Configuration> configurations);
    boolean exists(Configuration configuration);

    @Service
    @AllArgsConstructor(onConstructor_ = @Autowired)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    class ConfigurationServiceImpl implements ConfigurationService {

        private static final String CONFIGURATION_ENVIRONMENT_PROPERTY_SOURCE_NAME = "configurationEnvironment";

        ConfigurationRepository configurationRepository;
        ConfigurableEnvironment configurableEnvironment;

        @Override
        public PropertySource<?> getPropertySource() {
            Map<String, Object> configurationMap = configurationRepository.findAll().stream()
                    .collect(Collectors.toMap(Configuration::getKey, Configuration::getValue));
            return new MapPropertySource(CONFIGURATION_ENVIRONMENT_PROPERTY_SOURCE_NAME, configurationMap);
        }

        @Override
        public <T> T get(String key, Class<T> type) {
            return configurableEnvironment.getRequiredProperty(key, type);
        }

        @Override
        public Map<String, String> get() {
            return configurationRepository.findAll().stream()
                    .collect(Collectors.toMap(
                            Configuration::getKey,
                            Configuration::getValue,
                            (value1, value2) -> value2,
                            TreeMap::new
                    ));
        }

        @Override
        public Map<String, String> update(Collection<Configuration> configurations) {
            configurationRepository.saveAll(configurations);
            Map<String, Object> configurationMap = configurationRepository.findAll().stream()
                    .collect(Collectors.toMap(Configuration::getKey, Configuration::getValue));
            PropertySource<?> propertySource = new MapPropertySource(
                    CONFIGURATION_ENVIRONMENT_PROPERTY_SOURCE_NAME, configurationMap
            );
            configurableEnvironment.getPropertySources().replace(
                    CONFIGURATION_ENVIRONMENT_PROPERTY_SOURCE_NAME, propertySource
            );
            return configurationMap.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            entry -> entry.getValue().toString(),
                            (value1, value2) -> value2,
                            TreeMap::new
                    ));
        }

        @Override
        public boolean exists(Configuration configuration) {
            return configurableEnvironment.containsProperty(configuration.getKey());
        }
    }
}
