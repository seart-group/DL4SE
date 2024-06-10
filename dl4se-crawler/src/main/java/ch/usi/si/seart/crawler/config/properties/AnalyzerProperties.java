package ch.usi.si.seart.crawler.config.properties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.Duration;

@Getter
@ConfigurationProperties(prefix = "crawler.analyzer", ignoreUnknownFields = false)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @ConstructorBinding)
public class AnalyzerProperties {

    @PositiveOrZero
    int corePoolSize;

    @Positive
    int maxPoolSize;

    @PositiveOrZero
    int queueCapacity;

    Duration maxParseTime;
}
