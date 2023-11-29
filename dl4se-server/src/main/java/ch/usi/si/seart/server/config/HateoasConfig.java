package ch.usi.si.seart.server.config;

import ch.usi.si.seart.model.user.token.Token;
import ch.usi.si.seart.server.config.properties.WebsiteProperties;
import ch.usi.si.seart.server.hateoas.LinkGenerator;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.web.util.UriComponentsBuilder;

@Configuration
public class HateoasConfig {

    @Bean
    public LinkGenerator<Token> verificationLinkGenerator(UriComponentsBuilder uriComponentsBuilder) {
        return new TokenValueLastPathSegmentLinkGenerator(uriComponentsBuilder, "/verify");
    }

    @Bean
    public LinkGenerator<Token> passwordResetLinkGenerator(UriComponentsBuilder uriComponentsBuilder) {
        return new TokenValueLastPathSegmentLinkGenerator(uriComponentsBuilder, "/password/reset");
    }

    @Bean
    @Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    UriComponentsBuilder uriComponentsBuilder(WebsiteProperties properties) {
        return UriComponentsBuilder.fromHttpUrl(properties.getBaseURL().toString());
    }

    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    static final class TokenValueLastPathSegmentLinkGenerator implements LinkGenerator<Token> {

        UriComponentsBuilder uriComponentsBuilder;

        String path;

        @Override
        @SneakyThrows
        public String generate(Token token) {
            return uriComponentsBuilder.path(path + "/" + token.getValue())
                    .build()
                    .toUri()
                    .toURL()
                    .toString();
        }
    }
}
