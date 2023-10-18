package ch.usi.si.seart.crawler.config;

import ch.usi.si.seart.model.job.Job;
import com.google.api.client.http.GenericUrl;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.unit.DataSize;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.function.Predicate;

@Configuration
@ConfigurationPropertiesScan
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

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @ConfigurationProperties(prefix = "app.crawl-job.ignore.repository.file")
    public static class FileFilter implements Predicate<Path> {

        Predicate<Path> pathPredicate;

        Optional<DataSize> maxSize;

        Optional<Long> maxLines;

        Optional<Duration> maxParseTime;

        @ConstructorBinding
        public FileFilter(String globPattern, DataSize maxSize, Long maxLines, Duration maxParseTime) {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:" + globPattern);
            this.pathPredicate = Predicate.not(matcher::matches);
            this.maxSize = Optional.ofNullable(maxSize);
            this.maxLines = Optional.ofNullable(maxLines);
            this.maxParseTime = Optional.ofNullable(maxParseTime);
        }

        private boolean testPath(Path path) {
            return pathPredicate.test(path);
        }

        private boolean testSize(Path path) {
            return maxSize.map(maxSize -> {
                try {
                    long bytes = Files.size(path);
                    DataSize size = DataSize.ofBytes(bytes);
                    return size.compareTo(maxSize) < 0;
                } catch (NoSuchFileException ex) {
                    return false;
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }).orElse(true);
        }

        private boolean testLine(Path path) {
            return maxLines.map(maxLines -> {
                try (
                        FileReader fileReader = new FileReader(path.toFile());
                        BufferedReader bufferedReader = new BufferedReader(fileReader)
                ) {
                    int lines = 0;
                    while (bufferedReader.readLine() != null) {
                        lines++;
                        if (lines >= maxLines) return false;
                    }
                    return true;
                } catch (FileNotFoundException ex) {
                    return false;
                } catch (IOException ex) {
                    throw new UncheckedIOException(ex);
                }
            }).orElse(true);
        }

        @Override
        public boolean test(Path path) {
            return testPath(path) && testSize(path) && testLine(path);
        }
    }
}
