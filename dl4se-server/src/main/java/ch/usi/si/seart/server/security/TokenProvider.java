package ch.usi.si.seart.server.security;

import ch.usi.si.seart.server.config.properties.JwtProperties;
import io.jsonwebtoken.JwtBuilder;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

public interface TokenProvider {

    String generate(Authentication authentication);

    @Component
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    class JwtTokenProvider implements TokenProvider {

        JwtBuilder jwtBuilder;

        Duration validFor;

        @Autowired
        public JwtTokenProvider(JwtBuilder jwtBuilder, JwtProperties properties) {
            this.jwtBuilder = jwtBuilder;
            this.validFor = properties.getTokenProperties().getValidFor();
        }

        @Override
        public String generate(Authentication authentication) {
            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
            Long id = principal.getId();
            ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
            Instant issued = now.toInstant();
            Instant expires = now.plus(validFor).toInstant();
            return jwtBuilder
                    .subject(Long.toString(id))
                    .issuedAt(Date.from(issued))
                    .expiration(Date.from(expires))
                    .compact();
        }
    }
}
