package usi.si.seart.config;

import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import usi.si.seart.repository.UserRepository;
import usi.si.seart.security.jwt.JwtRequestFilter;
import usi.si.seart.security.jwt.JwtTokenProvider;

import java.security.Key;

@Configuration
public class JwtConfig {

    @Bean
    public Key secretKey(@Value("${jwt.secret}") String value) {
        return Keys.hmacShaKeyFor(value.getBytes());
    }

    @Bean
    public JwtBuilder jwtBuilder(Key secretKey) {
        return Jwts.builder().signWith(secretKey, SignatureAlgorithm.HS512);
    }

    @Bean
    public JwtParser jwtParser(Key secretKey) {
        return Jwts.parserBuilder().setSigningKey(secretKey).build();
    }

    @Bean
    public JwtTokenProvider jwtTokenProvider(JwtBuilder builder, JwtParser parser) {
        return new JwtTokenProvider(builder, parser);
    }

    @Bean
    public JwtRequestFilter jwtRequestFilter(JwtTokenProvider jwtTokenProvider, UserRepository userRepository) {
        return new JwtRequestFilter(jwtTokenProvider, userRepository);
    }
}
