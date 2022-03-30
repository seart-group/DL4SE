package usi.si.seart.security;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import usi.si.seart.model.user.Role;
import usi.si.seart.model.user.User;

import java.util.Collection;
import java.util.List;

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

    Role role;

    public static UserPrincipal of(User user) {
        return new UserPrincipal(user.getId(), user.getEmail(), user.getPassword(), user.getVerified(), user.getRole());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
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
        return verified;
    }
}
