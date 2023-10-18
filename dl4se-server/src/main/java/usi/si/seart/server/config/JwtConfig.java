package usi.si.seart.server.config;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import usi.si.seart.model.user.User;
import usi.si.seart.server.exception.UserNotFoundException;
import usi.si.seart.server.security.UserPrincipal;
import usi.si.seart.server.security.jwt.JwtRequestFilter;
import usi.si.seart.server.security.jwt.JwtTokenProvider;
import usi.si.seart.server.service.UserService;

import javax.crypto.SecretKey;
import java.time.Duration;

@Configuration
public class JwtConfig {

    @Bean
    public SecretKey secretKey(@Value("${jwt.secret}") String value) {
        return Keys.hmacShaKeyFor(value.getBytes());
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
    public JwtTokenProvider jwtTokenProvider(
            JwtBuilder builder, JwtParser parser, @Value("${jwt.token.valid-for}") Duration validFor
    ) {
        return new JwtTokenProvider(builder, parser, validFor);
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter(
            JwtTokenProvider jwtTokenProvider, UserService userService, ConversionService conversionService
    ) {
        return new JwtRequestFilter(jwtTokenProvider) {
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
