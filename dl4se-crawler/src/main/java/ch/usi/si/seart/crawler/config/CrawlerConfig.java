package ch.usi.si.seart.crawler.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.LocalDateTime;

@Configuration
public class CrawlerConfig {

    @Bean
    public Duration nextRunDelay(@Value("${app.crawl-job.next-run-delay}") String value) {
        return Duration.parse(value);
    }

    @Bean
    public LocalDateTime defaultStartDateTime(@Value("${app.crawl-job.start-date-time}") String value) {
        return LocalDateTime.parse(value);
    }
}
