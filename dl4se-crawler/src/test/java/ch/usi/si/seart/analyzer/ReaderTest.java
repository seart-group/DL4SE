package ch.usi.si.seart.analyzer;

import com.google.common.io.CharStreams;
import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

abstract class ReaderTest {

    protected abstract Reader getSubject(Reader initial);

    protected abstract String getInput();

    protected abstract String getExpected();

    @Test
    void testRead() throws IOException {
        String input = getInput();
        String expected = getExpected();
        @Cleanup Reader stringReader = new StringReader(input);
        @Cleanup Reader testSubject = getSubject(stringReader);
        String actual = CharStreams.toString(testSubject);
        Assertions.assertEquals(expected, actual);
    }
}
