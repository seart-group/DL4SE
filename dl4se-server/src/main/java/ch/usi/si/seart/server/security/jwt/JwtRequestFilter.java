package ch.usi.si.seart.server.security.jwt;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class JwtRequestFilter extends OncePerRequestFilter {

    JwtTokenProvider tokenProvider;

    protected abstract UserDetails getUserDetails(Long id);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        Optional.ofNullable(token)
                .filter(value -> value.startsWith("Bearer "))
                .map(value -> value.substring(7))
                .filter(tokenProvider::validateToken)
                .map(tokenProvider::getUserIdFromJWT)
                .map(this::getUserDetails)
                .filter(UserDetails::isEnabled)
                .ifPresent(userDetails -> {
                    WebAuthenticationDetails details = new WebAuthenticationDetailsSource().buildDetails(request);
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );
                    authentication.setDetails(details);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                });
        filterChain.doFilter(request, response);
    }
}
