package usi.si.seart.converter;

import com.google.common.base.Converter;
import com.google.common.io.CharStreams;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InputStreamToString extends Converter<InputStream, String> {

    @Getter
    private static final Converter<InputStream, String> converter = new InputStreamToString();

    @Override
    @SneakyThrows(IOException.class)
    protected String doForward(InputStream inputStream) {
        try (Reader reader = new InputStreamReader(inputStream)) {
            return CharStreams.toString(reader);
        }
    }

    @Override
    protected InputStream doBackward(String s) {
        throw new UnsupportedOperationException("Backwards conversion is not supported!");
    }
}
