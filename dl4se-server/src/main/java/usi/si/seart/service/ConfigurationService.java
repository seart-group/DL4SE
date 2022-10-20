package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;
import usi.si.seart.model.Configuration;
import usi.si.seart.repository.ConfigurationRepository;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public interface ConfigurationService {

    PropertySource<?> getPropertySource();
    Map<String, String> get();
    <T> T get(String key, Class<T> type);
    Map<String, String> update(Collection<Configuration> configurations);
    boolean exists(Configuration configuration);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    class ConfigurationServiceImpl implements ConfigurationService {

        ConfigurationRepository configurationRepository;
        ConfigurableEnvironment configurableEnvironment;

        String environmentName = "dl4seEnvironment";

        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        Lock readLock = readWriteLock.readLock();
        Lock writeLock = readWriteLock.writeLock();

        @Override
        public PropertySource<?> getPropertySource() {
            Map<String, Object> configurationMap = configurationRepository.findAll().stream()
                    .collect(Collectors.toMap(Configuration::getKey, Configuration::getValue));
            return new MapPropertySource(environmentName, configurationMap);
        }

        @Override
        public Map<String, String> get() {
            try {
                readLock.lock();
                return configurationRepository.findAll().stream()
                        .collect(Collectors.toMap(
                                Configuration::getKey,
                                Configuration::getValue,
                                (value1, value2) -> value2,
                                LinkedHashMap::new
                        ));
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public <T> T get(String key, Class<T> type) {
            try {
                readLock.lock();
                return configurableEnvironment.getRequiredProperty(key, type);
            } finally {
                readLock.unlock();
            }
        }

        @Override
        public Map<String, String> update(Collection<Configuration> configurations) {
            try {
                writeLock.lock();
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
                                LinkedHashMap::new
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
