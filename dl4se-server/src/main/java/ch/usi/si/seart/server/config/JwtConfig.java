package ch.usi.si.seart.server.config;

import ch.usi.si.seart.exception.UserNotFoundException;
import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.server.config.properties.JwtProperties;
import ch.usi.si.seart.server.security.UserPrincipal;
import ch.usi.si.seart.server.service.UserService;
import ch.usi.si.seart.server.web.filter.JwtRequestFilter;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import javax.crypto.SecretKey;

@Configuration
public class JwtConfig {

    @Bean
    SecretKey secretKey(JwtProperties properties) {
        return Keys.hmacShaKeyFor(properties.getSecret().getBytes());
    }

    @Bean
    public JwtBuilder jwtBuilder(SecretKey secretKey) {
        return Jwts.builder().signWith(secretKey, Jwts.SIG.HS512);
    }

    @Bean
    public JwtParser jwtParser(SecretKey secretKey) {
        return Jwts.parser().verifyWith(secretKey).build();
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter(
            JwtParser parser, UserService userService, ConversionService conversionService
    ) {
        return new JwtRequestFilter() {

            @Override
            protected Long getUserId(String value) {
                try {
                    Claims claims = parser.parseSignedClaims(value).getPayload();
                    return Long.valueOf(claims.getSubject());
                } catch (RuntimeException ignored) {
                    return null;
                }
            }

            @Override
            protected UserDetails getUserDetails(Long id) {
                try {
                    User user = userService.getWithId(id);
                    return conversionService.convert(user, UserPrincipal.class);
                } catch (UserNotFoundException ex) {
                    throw new UsernameNotFoundException(ex.getMessage());
                }
            }
        };
    }
}
