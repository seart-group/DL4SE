package usi.si.seart.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import usi.si.seart.dto.UserDto;
import usi.si.seart.model.user.User;

public class DtoToUserConverter implements Converter<UserDto, User> {

    @Override
    @NonNull
    public User convert(@NonNull UserDto source) {
        return User.builder()
                .email(source.getEmail())
                .password(source.getPassword())
                .organisation(source.getOrganisation())
                .build();
    }
}
