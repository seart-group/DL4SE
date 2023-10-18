package ch.usi.si.seart.server.bean;

import ch.usi.si.seart.model.Configuration;
import ch.usi.si.seart.server.repository.ConfigurationRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Properties;

@Component("ConfigurationInitializingBean")
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfigurationInitializingBean implements InitializingBean {

    ConfigurationRepository configurationRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        Resource resource = new ClassPathResource("application.properties");
        Properties properties = PropertiesLoaderUtils.loadProperties(resource);
        properties.entrySet().stream()
                .map(entry -> Map.entry(
                        entry.getKey().toString(),
                        entry.getValue().toString()
                ))
                .filter(entry -> entry.getKey().startsWith("configuration"))
                .map(entry -> Map.entry(
                        entry.getKey().substring(14),
                        entry.getValue()
                ))
                .filter(entry -> !configurationRepository.existsById(entry.getKey()))
                .map(entry -> new Configuration(entry.getKey(), entry.getValue()))
                .forEach(configurationRepository::save);
    }
}
