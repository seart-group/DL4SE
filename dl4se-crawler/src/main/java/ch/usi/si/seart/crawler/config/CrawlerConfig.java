package ch.usi.si.seart.crawler.config;

import ch.usi.si.seart.crawler.config.properties.CrawlerProperties;
import com.google.api.client.http.GenericUrl;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.time.Duration;

@Configuration
@ConfigurationPropertiesScan(basePackages = "ch.usi.si.seart.crawler.config.properties")
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
}
