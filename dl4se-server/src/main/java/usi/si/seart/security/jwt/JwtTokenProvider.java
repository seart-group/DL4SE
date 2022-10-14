package usi.si.seart.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import usi.si.seart.security.UserPrincipal;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;


@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class JwtTokenProvider {

    JwtBuilder jwtBuilder;
    JwtParser jwtParser;

    public String generateToken(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        Long id = principal.getId();
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        Date issue = Date.from(now.toInstant());
        Date expiry = Date.from(now.plusDays(7).toInstant());
        return jwtBuilder
                .setSubject(Long.toString(id))
                .setIssuedAt(issue)
                .setExpiration(expiry)
                .compact();
    }

    public Long getUserIdFromJWT(String value) {
        Claims claims = jwtParser.parseClaimsJws(value).getBody();
        return Long.valueOf(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }
}
