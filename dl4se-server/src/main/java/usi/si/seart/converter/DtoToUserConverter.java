package usi.si.seart.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import usi.si.seart.dto.RegisterDto;
import usi.si.seart.model.user.User;

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
