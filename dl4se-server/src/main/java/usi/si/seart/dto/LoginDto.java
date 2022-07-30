package usi.si.seart.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import usi.si.seart.validation.constraints.OWASPEmail;
import usi.si.seart.validation.constraints.Password;

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
