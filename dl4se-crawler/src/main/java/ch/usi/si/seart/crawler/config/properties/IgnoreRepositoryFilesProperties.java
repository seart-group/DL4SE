package ch.usi.si.seart.crawler.config.properties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.PositiveOrZero;

@Validated
@Getter
@ConfigurationProperties(prefix = "crawler.ignore.repository.files", ignoreUnknownFields = false)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @ConstructorBinding)
public class IgnoreRepositoryFilesProperties {

    DataSize maxSize;

    @PositiveOrZero
    Long maxLines;

    String globPattern;
}
