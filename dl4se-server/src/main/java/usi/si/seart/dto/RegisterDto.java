package usi.si.seart.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import usi.si.seart.validation.constraints.OWASPEmail;
import usi.si.seart.validation.constraints.Password;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

@Getter
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto implements Serializable {

    @OWASPEmail
    String email;

    @Password
    String password;

    @NotBlank
    String organisation;
}
