package ch.usi.si.seart.crawler.config;

import ch.usi.si.seart.bean.CrawlJobInitializingBean;
import ch.usi.si.seart.crawler.config.properties.CrawlerProperties;
import ch.usi.si.seart.crawler.service.FileSystemService;
import ch.usi.si.seart.model.job.Job;
import com.google.api.client.http.GenericUrl;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.Scope;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.time.Duration;

@Configuration
@ConfigurationPropertiesScan(basePackages = "ch.usi.si.seart.crawler.config.properties")
@PropertySource("classpath:crawler.properties")
public class CrawlerConfig {

    @Bean
    public Duration nextRunDelay(CrawlerProperties properties) {
        return properties.getNextRunDelay();
    }

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
}
