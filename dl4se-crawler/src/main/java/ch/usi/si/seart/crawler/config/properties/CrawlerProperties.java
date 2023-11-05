package ch.usi.si.seart.crawler.config.properties;

import com.google.api.client.http.GenericUrl;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Validated
@Getter
@ConfigurationProperties(prefix = "crawler")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @ConstructorBinding)
public class CrawlerProperties {

    @NotNull
    String tmpDirPrefix;

    GenericUrl baseUrl;

    @Past
    LocalDate startDate;

    @NestedConfigurationProperty
    SchedulingProperties scheduling;

    @NestedConfigurationProperty
    AnalyzerProperties analyzer;

    @NestedConfigurationProperty
    IgnoreProperties ignore;

    @NotEmpty
    Map<@NotBlank String, @NotEmpty List<@NotBlank String>> languages;

    public MultiValueMap<String, String> getLanguages() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>(languages);
        return CollectionUtils.unmodifiableMultiValueMap(map);
    }
}
