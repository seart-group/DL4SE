package ch.usi.si.seart.server.config.properties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

@Getter
@ConfigurationProperties(prefix = "platform.jwt", ignoreUnknownFields = false)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @ConstructorBinding)
public class JwtProperties {

    @NotBlank
    String secret;

    TokenProperties tokens;

    public TokenProperties getTokenProperties() {
        return tokens;
    }

    @Getter
    @AllArgsConstructor(access = AccessLevel.PACKAGE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public static class TokenProperties {

        @NotNull
        Duration validFor;
    }
}
