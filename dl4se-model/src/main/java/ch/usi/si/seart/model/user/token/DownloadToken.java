package ch.usi.si.seart.model.user.token;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Entity
@DiscriminatorValue("DOWNLOAD")
@Getter
@Setter
@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DownloadToken extends Token {

    @Override
    protected LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now(ZoneOffset.UTC).plusMinutes(1);
    }
}
