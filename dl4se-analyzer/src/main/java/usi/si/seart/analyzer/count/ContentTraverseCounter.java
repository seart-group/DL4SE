package usi.si.seart.analyzer.count;

import usi.si.seart.treesitter.Range;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public abstract class ContentTraverseCounter extends TraverseCounter {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_16LE;

    private byte[] bytes;
    private boolean ready;

    protected ContentTraverseCounter() {
        super();
        this.bytes = new byte[0];
        this.ready = false;
    }

    protected ContentTraverseCounter(byte[] bytes) {
        super();
        this.bytes = bytes;
        this.ready = true;
    }

    protected boolean isReady() {
        return ready;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
        this.ready = bytes.length > 0;
    }

    private byte[] getBytesForRange(Range range) {
        int startByte = range.getStartByte() * 2;
        int endByte = range.getEndByte() * 2;
        return Arrays.copyOfRange(this.bytes, startByte, endByte);
    }

    protected final String getContentForRange(Range range) {
        byte[] bytes = getBytesForRange(range);
        return new String(bytes, DEFAULT_CHARSET);
    }
}
