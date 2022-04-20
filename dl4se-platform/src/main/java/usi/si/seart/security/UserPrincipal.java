package usi.si.seart.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import usi.si.seart.model.user.Role;
import usi.si.seart.model.user.User;

import java.util.Collection;
import java.util.List;

@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserPrincipal implements UserDetails {

    @Getter
    Long id;

    @Getter(onMethod_ = @Override)
    String username;

    @Getter(onMethod_ = @Override)
    String password;

    Boolean verified;

    Boolean enabled;

    Role role;

    public static UserPrincipal of(User user) {
        return UserPrincipal.builder()
                .id(user.getId())
                .username(user.getEmail())
                .password(user.getPassword())
                .verified(user.getVerified())
                .enabled(user.getEnabled())
                .role(user.getRole())
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public String getEmail() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return verified && enabled;
    }
}
