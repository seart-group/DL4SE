package ch.usi.si.seart.crawler.config.properties;

import com.google.api.client.http.GenericUrl;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.time.Duration;

@Getter
@ConfigurationProperties(prefix = "crawler")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @ConstructorBinding)
public class CrawlerProperties {

    String tmpDirPrefix;

    Duration nextRunDelay;

    GenericUrl baseUrl;
}
