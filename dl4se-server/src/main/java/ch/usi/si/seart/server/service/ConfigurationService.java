package ch.usi.si.seart.server.service;

import ch.usi.si.seart.model.Configuration;
import ch.usi.si.seart.repository.ConfigurationRepository;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public interface ConfigurationService {

    PropertySource<?> getPropertySource();
    <T> T get(String key, Class<T> type);
    Map<String, String> get();
    Map<String, String> update(Collection<Configuration> configurations);
    boolean exists(Configuration configuration);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    class ConfigurationServiceImpl implements ConfigurationService {

        private static final String environmentName = "configurationEnvironment";

        ConfigurationRepository configurationRepository;
        ConfigurableEnvironment configurableEnvironment;

        Lock readLock;
        Lock writeLock;

        @Autowired
        public ConfigurationServiceImpl(
                ConfigurationRepository configurationRepository, ConfigurableEnvironment configurableEnvironment
        ) {
            this(configurationRepository, configurableEnvironment, new ReentrantReadWriteLock());
        }

        private ConfigurationServiceImpl(
                ConfigurationRepository configurationRepository,
                ConfigurableEnvironment configurableEnvironment,
                ReadWriteLock readWriteLock
        ) {
            this.configurationRepository = configurationRepository;
            this.configurableEnvironment = configurableEnvironment;
            this.readLock = readWriteLock.readLock();
            this.writeLock = readWriteLock.writeLock();
        }

        @Override
        public PropertySource<?> getPropertySource() {
            Map<String, Object> configurationMap = configurationRepository.findAll().stream()
                    .collect(Collectors.toMap(Configuration::getKey, Configuration::getValue));
            return new MapPropertySource(environmentName, configurationMap);
        }

        @Override
        public <T> T get(String key, Class<T> type) {
            readLock.lock();
            try {
                return configurableEnvironment.getRequiredProperty(key, type);
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public Map<String, String> get() {
            readLock.lock();
            try {
                return configurationRepository.findAll().stream()
                        .collect(Collectors.toMap(
                                Configuration::getKey,
                                Configuration::getValue,
                                (value1, value2) -> value2,
                                TreeMap::new
                        ));
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public Map<String, String> update(Collection<Configuration> configurations) {
            writeLock.lock();
            try {
                configurationRepository.saveAll(configurations);
                Map<String, Object> configurationMap = configurationRepository.findAll().stream()
                        .collect(Collectors.toMap(Configuration::getKey, Configuration::getValue));
                PropertySource<?> propertySource = new MapPropertySource(environmentName, configurationMap);
                configurableEnvironment.getPropertySources().replace(environmentName, propertySource);
                return configurationMap.entrySet().stream()
                        .collect(Collectors.toMap(
                                Map.Entry::getKey,
                                entry -> entry.getValue().toString(),
                                (value1, value2) -> value2,
                                TreeMap::new
                        ));
            } finally {
                writeLock.unlock();
            }
        }

        @Override
        public boolean exists(Configuration configuration) {
            return configurableEnvironment.containsProperty(configuration.getKey());
        }
    }
}
