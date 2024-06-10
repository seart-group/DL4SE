package ch.usi.si.seart.server.converter;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.server.security.UserPrincipal;
import org.springframework.core.convert.converter.Converter;

public class UserToUserPrincipalConverter implements Converter<User, UserPrincipal> {
    
    @Override
    public UserPrincipal convert(User source) {
        return UserPrincipal.builder()
                .id(source.getId())
                .uid(source.getUid())
                .username(source.getEmail())
                .password(source.getPassword())
                .verified(source.getVerified())
                .enabled(source.getEnabled())
                .role(source.getRole())
                .build();
    }
}
