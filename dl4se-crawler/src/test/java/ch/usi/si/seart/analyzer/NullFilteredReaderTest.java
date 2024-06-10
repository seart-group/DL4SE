package ch.usi.si.seart.analyzer;

import java.io.Reader;

class NullFilteredReaderTest extends ReaderTest {

    @Override
    protected Reader getSubject(Reader initial) {
        return new NullFilteredReader(initial);
    }

    @Override
    protected String getInput() {
        return "This\0is\0a\0test!";
    }

    @Override
    protected String getExpected() {
        return "Thisisatest!";
    }
}
