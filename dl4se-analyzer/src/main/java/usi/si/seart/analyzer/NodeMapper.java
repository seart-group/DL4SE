package usi.si.seart.analyzer;

import ch.usi.si.seart.treesitter.Range;

import java.util.Arrays;

public interface NodeMapper {

    byte[] getBytes();

    default byte[] getBytesForRange(Range range) {
        int startByte = range.getStartByte() * 2;
        int endByte = range.getEndByte() * 2;
        return Arrays.copyOfRange(getBytes(), startByte, endByte);
    }

    default String getContentForRange(Range range) {
        byte[] bytes = getBytesForRange(range);
        return new String(bytes, Settings.DEFAULT_CHARSET);
    }
}
