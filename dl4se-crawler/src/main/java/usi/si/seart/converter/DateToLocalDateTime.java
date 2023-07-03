package usi.si.seart.converter;

import com.google.common.base.Converter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateToLocalDateTime extends Converter<Date, LocalDateTime> {

    @Getter
    private static final Converter<Date, LocalDateTime> converter = new DateToLocalDateTime();

    @Override
    protected LocalDateTime doForward(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }

    @Override
    protected Date doBackward(LocalDateTime ldt) {
        return new Date(ldt.toInstant(ZoneOffset.UTC).toEpochMilli());
    }
}
