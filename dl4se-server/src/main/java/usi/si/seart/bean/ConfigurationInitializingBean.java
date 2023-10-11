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
import java.util.Optional;
import java.util.function.Supplier;

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
        map.forEach((key, value) -> {
            Optional<Configuration> optional = configurationRepository.findById(key);
            Supplier<Configuration> supplier = () -> new Configuration(key, value);
            Configuration configuration = optional.orElseGet(supplier);
            configuration.setValue(value);
            configurationRepository.save(configuration);
        });
    }
}
