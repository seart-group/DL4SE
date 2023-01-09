package usi.si.seart.analyzer.predicate;

import usi.si.seart.treesitter.Range;

import java.util.Arrays;

public abstract class ContentPredicate implements Predicate {

    private byte[] bytes;
    private boolean ready;

    protected ContentPredicate() {
        this.bytes = new byte[0];
        this.ready = false;
    }

    protected ContentPredicate(byte[] bytes) {
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

    protected byte[] getBytesForRange(Range range) {
        int startByte = range.getStartByte() * 2;
        int endByte = range.getEndByte() * 2;
        return Arrays.copyOfRange(this.bytes, startByte, endByte);
    }
}
