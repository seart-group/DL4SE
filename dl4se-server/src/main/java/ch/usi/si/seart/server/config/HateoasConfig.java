package ch.usi.si.seart.server.config;

import ch.usi.si.seart.model.user.token.PasswordResetToken;
import ch.usi.si.seart.model.user.token.Token;
import ch.usi.si.seart.model.user.token.VerificationToken;
import ch.usi.si.seart.server.config.properties.WebsiteProperties;
import ch.usi.si.seart.server.hateoas.URLGenerator;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.util.UriBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.net.URL;

@Configuration
public class HateoasConfig {

    @Bean
    public URLGenerator<VerificationToken> verificationURLGenerator(UriBuilder uriBuilder) {
        return new TokenLinkGenerator<>(uriBuilder, "verify");
    }

    @Bean
    public URLGenerator<PasswordResetToken> passwordResetURLGenerator(UriBuilder uriBuilder) {
        return new TokenLinkGenerator<>(uriBuilder, "password", "reset");
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    UriBuilder uriBuilder(WebsiteProperties properties) {
        return UriComponentsBuilder.fromHttpUrl(properties.getBaseURL().toString());
    }

    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    static final class TokenLinkGenerator<T extends Token> implements URLGenerator<T> {

        UriBuilder uriComponentsBuilder;

        String[] prefixSegments;

        public TokenLinkGenerator(UriBuilder uriComponentsBuilder, String... prefixSegments) {
            this.uriComponentsBuilder = uriComponentsBuilder;
            this.prefixSegments = prefixSegments;
        }

        @Override
        public URL generate(Token token) {
            try {
                return uriComponentsBuilder
                        .pathSegment(prefixSegments)
                        .pathSegment(token.getValue())
                        .build()
                        .toURL();
            } catch (MalformedURLException ex) {
                throw new IllegalStateException("URL construction failed", ex);
            }
        }
    }
}
