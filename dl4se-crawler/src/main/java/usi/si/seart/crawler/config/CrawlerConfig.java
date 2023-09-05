package usi.si.seart.crawler.config;

import com.google.api.client.http.GenericUrl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import usi.si.seart.model.job.Job;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Predicate;

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

    @Bean
    public Predicate<Path> analyzeFilePredicate(@Value("${app.crawl-job.ignore.file-pattern}") String value) {
        PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + value);
        return Predicate.not(pathMatcher::matches);
    }
}
