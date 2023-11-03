package ch.usi.si.seart.crawler.config.properties;

import com.google.api.client.http.GenericUrl;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.Duration;
import java.time.LocalDate;

@Validated
@Getter
@ConfigurationProperties(prefix = "crawler")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @ConstructorBinding)
public class CrawlerProperties {

    @NotNull
    String tmpDirPrefix;

    Duration nextRunDelay;

    GenericUrl baseUrl;

    @Past
    LocalDate startDate;

    @NestedConfigurationProperty
    AnalyzerProperties analyzer;
}
