package ch.usi.si.seart.server.config.properties;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Duration;

@Getter
@ConfigurationProperties(prefix = "platform.jwt", ignoreUnknownFields = false)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtProperties {

    private static final int MIN_LENGTH = 32;
    private static final String DEFAULT = RandomStringUtils.random(MIN_LENGTH, true, true);

    @NotBlank
    String secret;

    TokenProperties tokens;

    @ConstructorBinding
    public JwtProperties(String secret, TokenProperties tokens) {
        this.secret = StringUtils.isBlank(secret) ? DEFAULT : secret;
        this.tokens = tokens;
    }

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
