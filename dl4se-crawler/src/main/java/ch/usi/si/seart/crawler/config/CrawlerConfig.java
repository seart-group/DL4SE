package ch.usi.si.seart.crawler.config;

import ch.usi.si.seart.bean.CrawlJobInitializingBean;
import ch.usi.si.seart.crawler.bean.LanguageInitializingBean;
import ch.usi.si.seart.crawler.config.properties.CrawlerProperties;
import ch.usi.si.seart.crawler.service.FileSystemService;
import ch.usi.si.seart.model.job.Job;
import com.google.api.client.http.GenericUrl;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;

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
    public CrawlJobInitializingBean crawlJobInitializingBean(CrawlerProperties properties) {
        return new CrawlJobInitializingBean(Job.CODE, properties.getStartDate().atStartOfDay());
    }

    @Bean
    public LanguageInitializingBean languageInitializingBean(CrawlerProperties properties) {
        return new LanguageInitializingBean(properties.getLanguages());
    }
}
