package usi.si.seart.converter;

import org.springframework.core.convert.converter.Converter;
import usi.si.seart.model.user.User;
import usi.si.seart.security.UserPrincipal;

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
