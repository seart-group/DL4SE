package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;
import org.springframework.stereotype.Service;
import usi.si.seart.model.Configuration;
import usi.si.seart.repository.ConfigurationRepository;

import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;

public interface ConfigurationService {

    PropertySource<?> getPropertySource();
    <T> T get(String key, Class<T> type);
    Configuration modify(Configuration configuration);
    boolean exists(Configuration configuration);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @RequiredArgsConstructor(onConstructor_ = @Autowired)
    class ConfigurationServiceImpl implements ConfigurationService {

        ConfigurationRepository configurationRepository;
        ConfigurableEnvironment configurableEnvironment;

        @NonFinal
        @Value("${environment.name}")
        String environmentName;

        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        Lock readLock = readWriteLock.readLock();
        Lock writeLock = readWriteLock.writeLock();

        @Override
        public PropertySource<?> getPropertySource() {
            Map<String, Object> configurationMap = configurationRepository.findAll().stream()
                    .map(configuration -> Map.entry(configuration.getKey(), configuration.getValue()))
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return new MapPropertySource(environmentName, configurationMap);
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
        public Configuration modify(Configuration configuration) {
            try {
                writeLock.lock();
                configuration = configurationRepository.save(configuration);
                Map<String, Object> configurationMap = configurationRepository.findAll().stream()
                        .map(conf -> Map.entry(conf.getKey(), conf.getValue()))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                PropertySource<?> propertySource = new MapPropertySource(environmentName, configurationMap);
                configurableEnvironment.getPropertySources().replace(environmentName, propertySource);
                return configuration;
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
