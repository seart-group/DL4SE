package usi.si.seart.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import usi.si.seart.security.UserPrincipal;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;


@Component
public class JwtTokenProvider {

    @Value("${app.jwt.secret}")
    String secret;

    public String generateToken(Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        Date issue = Date.from(now.toInstant());
        Date expiry = Date.from(now.plusDays(7).toInstant());

        return Jwts.builder()
                .setSubject(Long.toString(principal.getId()))
                .setIssuedAt(issue)
                .setExpiration(expiry)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public Long getUserIdFromJWT(String value) {
        Claims claims = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(value)
                .getBody();

        return Long.valueOf(claims.getSubject());
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(secret).parseClaimsJws(token);
            return true;
        } catch (RuntimeException ignored) {
            return false;
        }
    }
}
