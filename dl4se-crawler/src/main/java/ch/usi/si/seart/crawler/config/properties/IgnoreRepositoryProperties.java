package ch.usi.si.seart.crawler.config.properties;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotBlank;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Validated
@Getter
@ConfigurationProperties(prefix = "crawler.ignore.repository")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class IgnoreRepositoryProperties {

    Set<@NotBlank String> names;

    @NestedConfigurationProperty
    IgnoreRepositoryFileProperties file;

    @ConstructorBinding
    public IgnoreRepositoryProperties(List<String> names, IgnoreRepositoryFileProperties file) {
        this.names = names.stream().collect(Collectors.collectingAndThen(
                Collectors.toSet(),
                Collections::unmodifiableSet
        ));
        this.file = file;
    }
}
