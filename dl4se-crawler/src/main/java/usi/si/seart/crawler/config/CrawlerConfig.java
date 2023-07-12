package usi.si.seart.crawler.config;

import com.google.api.client.http.GenericUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import usi.si.seart.model.job.Job;

import java.time.Duration;
import java.time.LocalDateTime;

@Configuration
public class CrawlerConfig {

    @Bean
    public Job jobType(@Value("${app.crawl-job.type}") Job jobType) {
        return jobType;
    }

    @Bean
    public Duration nextRunDelay(@Value("${app.crawl-job.next-run-delay}") String value) {
        return Duration.parse(value);
    }

    @Bean
    public GenericUrl baseUrl(@Value("${app.crawl-job.url}") String url) {
        return new GenericUrl(url);
    }

    @Bean
    public LocalDateTime defaultStartDateTime(@Value("${app.crawl-job.start-date-time}") String value) {
        return LocalDateTime.parse(value);
    }
}
