package ch.usi.si.seart.server.config;

import ch.usi.si.seart.model.user.token.PasswordResetToken;
import ch.usi.si.seart.model.user.token.Token;
import ch.usi.si.seart.model.user.token.VerificationToken;
import ch.usi.si.seart.server.config.properties.WebsiteProperties;
import ch.usi.si.seart.server.hateoas.URLGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class HateoasConfig {

    @Bean
    public URLGenerator<VerificationToken> verificationURLGenerator(WebsiteProperties properties) {
        return new TokenLinkGenerator<>(properties.getBaseURL().toString(), "verify");
    }

    @Bean
    public URLGenerator<PasswordResetToken> passwordResetURLGenerator(WebsiteProperties properties) {
        return new TokenLinkGenerator<>(properties.getBaseURL().toString(), "password", "reset");
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    static final class TokenLinkGenerator<T extends Token> implements URLGenerator<T> {

        String base;

        String[] prefixSegments;

        public TokenLinkGenerator(String base, String... prefixSegments) {
            this.base = base;
            this.prefixSegments = prefixSegments;
        }

        @Override
        public URL generate(Token token) {
            try {
                return UriComponentsBuilder.fromHttpUrl(base)
                        .pathSegment(prefixSegments)
                        .pathSegment(token.getValue())
                        .build()
                        .toUri()
                        .toURL();
            } catch (MalformedURLException ex) {
                throw new IllegalStateException("URL construction failed", ex);
            }
        }
    }
}
