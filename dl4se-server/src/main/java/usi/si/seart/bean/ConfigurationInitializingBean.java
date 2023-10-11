package usi.si.seart.bean;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;
import usi.si.seart.model.Configuration;
import usi.si.seart.repository.ConfigurationRepository;

import java.io.InputStream;
import java.util.Map;

@Component("ConfigurationInitializingBean")
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ConfigurationInitializingBean implements InitializingBean {

    ConfigurationRepository configurationRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        Resource resource = new ClassPathResource("configurations.yaml");
        InputStream inputStream = resource.getInputStream();
        Map<String, String> map = new Yaml().load(inputStream);
        map.entrySet().stream()
                .filter(entry -> {
                    String key = entry.getKey();
                    return !configurationRepository.existsById(key);
                })
                .forEach(entry -> {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    Configuration configuration = new Configuration(key, value);
                    configurationRepository.save(configuration);
                });
    }
}
