package usi.si.seart.converter;

import com.google.common.base.Converter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DateToLDTConverter extends Converter<Date, LocalDateTime> {

    @Getter
    public static final Converter<Date, LocalDateTime> instance = new DateToLDTConverter();

    @Override
    protected LocalDateTime doForward(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneOffset.UTC);
    }

    @Override
    protected Date doBackward(LocalDateTime ldt) {
        return new Date(ldt.toInstant(ZoneOffset.UTC).toEpochMilli());
    }
}
