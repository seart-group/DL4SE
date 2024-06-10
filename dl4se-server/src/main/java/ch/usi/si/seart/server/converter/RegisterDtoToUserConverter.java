package ch.usi.si.seart.server.converter;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.server.dto.RegisterDto;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;

public class RegisterDtoToUserConverter implements Converter<RegisterDto, User> {

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
