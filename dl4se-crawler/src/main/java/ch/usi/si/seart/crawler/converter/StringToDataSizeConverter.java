package ch.usi.si.seart.crawler.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;

@Component
public class StringToDataSizeConverter implements Converter<String, DataSize> {

    @Override
    public DataSize convert(String source) {
        return DataSize.parse(source);
    }
}
