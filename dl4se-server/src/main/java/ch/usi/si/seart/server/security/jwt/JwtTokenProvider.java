package ch.usi.si.seart.server.security.jwt;

import ch.usi.si.seart.server.security.UserPrincipal;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;


@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtTokenProvider {

    JwtBuilder jwtBuilder;
    JwtParser jwtParser;

    Duration validFor;

    public String generateToken(Authentication authentication) {
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

    public Long getUserIdFromJWT(String value) {
        Claims claims = jwtParser.parseSignedClaims(value).getPayload();
        return Long.valueOf(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseSignedClaims(token);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }
}
