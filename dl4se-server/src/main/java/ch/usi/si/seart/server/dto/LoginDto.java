package ch.usi.si.seart.server.dto;

import ch.usi.si.seart.validation.constraints.OWASPEmail;
import ch.usi.si.seart.validation.constraints.Password;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class LoginDto {

    @OWASPEmail
    String email;

    @Password
    String password;
}
