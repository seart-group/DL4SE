package usi.si.seart.config;

import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class MainConfig {

    @Bean
    @SneakyThrows
    SecurityFilterChain filterChain(HttpSecurity http) {
        return http.requiresChannel(channel -> channel.anyRequest().requiresSecure())
                .authorizeRequests(authorize -> authorize.anyRequest().permitAll())
                .antMatcher("/**")
                .build();
    }
}
