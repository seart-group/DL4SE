package ch.usi.si.seart.crawler.config.properties;

import ch.usi.si.seart.validation.constraints.FileExtension;
import ch.usi.si.seart.validation.constraints.LanguageName;
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

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
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

    @Pattern(regexp = "^https://[\\w-]+\\.(?:[\\w-]+\\.)*[\\w/-]+$")
    String baseUrl;

    @Past
    LocalDate startDate;

    @NestedConfigurationProperty
    SchedulingProperties scheduling;

    @NestedConfigurationProperty
    AnalyzerProperties analyzer;

    @NestedConfigurationProperty
    IgnoreProperties ignore;

    @NotEmpty
    Map<@LanguageName String, @NotEmpty List<@FileExtension String>> languages;

    public GenericUrl getBaseUrl() {
        return new GenericUrl(baseUrl);
    }

    public MultiValueMap<String, String> getLanguages() {
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>(languages);
        return CollectionUtils.unmodifiableMultiValueMap(map);
    }
}
