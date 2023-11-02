package ch.usi.si.seart.crawler.converter;

import com.google.api.client.http.GenericUrl;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToGenericUrlConverter implements Converter<String, GenericUrl> {

    @Override
    public GenericUrl convert(String source) {
        return new GenericUrl(source);
    }
}
