package ch.usi.si.seart.crawler.converter;

import org.springframework.boot.convert.DurationStyle;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class StringToDurationConverter implements Converter<String, Duration> {

    @Override
    public Duration convert(String source) {
        return DurationStyle.detectAndParse(source);
    }
}
