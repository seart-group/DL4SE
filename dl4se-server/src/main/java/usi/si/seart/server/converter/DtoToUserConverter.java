package usi.si.seart.server.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import usi.si.seart.model.user.User;
import usi.si.seart.server.dto.RegisterDto;

public class DtoToUserConverter implements Converter<RegisterDto, User> {

    @Override
    @NonNull
    public User convert(@NonNull RegisterDto source) {
        return User.builder()
                .email(source.getEmail())
                .password(source.getPassword())
                .organisation(source.getOrganisation())
                .build();
    }
}
