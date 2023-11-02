package ch.usi.si.seart.crawler.config.properties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.util.unit.DataSize;

import javax.validation.constraints.PositiveOrZero;

@Getter
@ConfigurationProperties(prefix = "app.crawl-job.ignore.repository.file")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @ConstructorBinding)
public class IgnoreRepositoryFileProperties {

    DataSize maxSize;

    @PositiveOrZero
    Long maxLines;

    String globPattern;
}