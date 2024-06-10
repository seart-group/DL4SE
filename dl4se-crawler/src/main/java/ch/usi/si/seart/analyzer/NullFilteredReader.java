package ch.usi.si.seart.analyzer;

import java.io.FilterReader;
import java.io.IOException;
import java.io.Reader;

class NullFilteredReader extends FilterReader {

    NullFilteredReader(Reader in) {
        super(in);
    }

    @Override
    public int read() throws IOException {
        int current;
        do current = super.read();
        while (current == 0);
        return current;
    }

    @Override
    public int read(char[] cbuf, int off, int len) throws IOException {
        int read = super.read(cbuf, off, len);
        if (read == -1) return -1;
        int pos = off - 1;
        for (int readPos = off; readPos < off + read; readPos++) {
            if (cbuf[readPos] == 0) {
                continue;
            } else {
                pos++;
            }

            if (pos < readPos) {
                cbuf[pos] = cbuf[readPos];
            }
        }
        return pos - off + 1;
    }
}
