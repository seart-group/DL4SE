package usi.si.seart.config;

import org.owasp.encoder.Encode;
import org.springframework.context.annotation.Bean;
import org.springframework.core.convert.ConversionService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import usi.si.seart.exception.UserNotFoundException;
import usi.si.seart.model.user.User;
import usi.si.seart.security.UserPrincipal;
import usi.si.seart.security.jwt.JwtRequestFilter;
import usi.si.seart.service.UserService;

import javax.servlet.http.HttpServletResponse;

@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity httpSecurity,
            AuthenticationEntryPoint authenticationEntryPoint,
            JwtRequestFilter jwtRequestFilter
    ) throws Exception {
        httpSecurity.cors()
                .and()
                .csrf()
                .disable()
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/actuator/**")
                .hasRole("ADMIN")
                .antMatchers(
                        "/",
                        "/user/login",
                        "/user/register",
                        "/user/verify",
                        "/user/verify/resend",
                        "/user/password/forgotten",
                        "/user/password/reset",
                        "/statistics/code",
                        "/statistics/users",
                        "/statistics/repos",
                        "/statistics/files",
                        "/statistics/functions",
                        "/statistics/languages/repos",
                        "/statistics/languages/files",
                        "/statistics/languages/functions",
                        "/statistics/tasks",
                        "/statistics/tasks/status",
                        "/statistics/tasks/size",
                        "/task/download/**")
                .permitAll()
                .anyRequest()
                .authenticated();

        httpSecurity.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            int code = HttpServletResponse.SC_UNAUTHORIZED;
            String message = Encode.forHtml(authException.getMessage());
            response.sendError(code, message);
        };
    }

    @Bean
    public UserDetailsService userDetailsService(UserService userService, ConversionService conversionService) {
        return username -> {
            try {
                User user = userService.getWithEmail(username);
                return conversionService.convert(user, UserPrincipal.class);
            } catch (UserNotFoundException ex) {
                throw new UsernameNotFoundException(ex.getMessage());
            }
        };
    }

    @Bean
    public UserDetailsPasswordService userDetailsPasswordService(
            PasswordEncoder passwordEncoder, UserService userService, ConversionService conversionService
    ) {
        return (userDetails, newPassword) -> {
            String email = userDetails.getUsername();
            User user = userService.getWithEmail(email);
            user.setPassword(passwordEncoder.encode(newPassword));
            user = userService.update(user);
            return conversionService.convert(user, UserPrincipal.class);
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(
            PasswordEncoder passwordEncoder, UserDetailsService userDetailsService
    ) {
        return authentication -> {
            String username = authentication.getName();
            String password = authentication.getCredentials().toString();
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            boolean enabled = userDetails.isEnabled();
            if (!enabled)
                throw new DisabledException("User is currently disabled!");
            if (passwordEncoder.matches(password, userDetails.getPassword()))
                return new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            else
                throw new BadCredentialsException("Incorrect password!");
        };
    }
}
