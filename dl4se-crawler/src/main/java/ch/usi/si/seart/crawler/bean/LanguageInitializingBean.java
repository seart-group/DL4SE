package ch.usi.si.seart.crawler.bean;

import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.repository.LanguageRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LanguageInitializingBean implements InitializingBean {

    LanguageRepository languageRepository;

    @Override
    public void afterPropertiesSet() throws Exception {
        Resource resource = new ClassPathResource("languages.yaml");
        InputStream inputStream = resource.getInputStream();
        Map<String, List<String>> map = new Yaml().load(inputStream);
        map.forEach((name, extensions) -> {
            Optional<Language> optional = languageRepository.findByNameIgnoreCase(name);
            Supplier<Language> supplier = () -> Language.builder().name(name).build();
            Language language = optional.orElseGet(supplier);
            language.setExtensions(extensions);
            languageRepository.save(language);
        });
    }
}
