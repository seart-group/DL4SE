package ch.usi.si.seart.crawler.config;

import ch.usi.si.seart.crawler.config.properties.CrawlerProperties;
import ch.usi.si.seart.crawler.service.FileSystemService;
import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.repository.LanguageRepository;
import com.google.api.client.http.GenericUrl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Configuration
public class CrawlerConfig {

    @Bean
    @Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public GenericUrl baseUrl(CrawlerProperties properties) {
        return properties.getBaseUrl().clone();
    }

    @Bean
    public FileSystemService fileSystemService(CrawlerProperties properties) {
        String prefix = properties.getTmpDirPrefix();
        return () -> {
            try {
                return Files.createTempDirectory(prefix);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        };
    }

    @Bean
    public InitializingBean languageInitializingBean(
            CrawlerProperties properties, LanguageRepository languageRepository
    ) {
        return () -> {
            MultiValueMap<String, String> languages = properties.getLanguages();
            for (Map.Entry<String, List<String>> entry : languages.entrySet()) {
                String name = entry.getKey();
                List<String> extensions = entry.getValue();
                Optional<Language> optional = languageRepository.findByNameIgnoreCase(name);
                Language language = optional.orElseGet(() -> Language.builder().name(name).build());
                language.setExtensions(extensions);
                languageRepository.save(language);
            }
        };
    }
}
