package ch.usi.si.seart.util.unit;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class ReadableFileSize {

    private static final double UNIT = 1024.0;

    long bytes;

    @Override
    public String toString() {
        // https://stackoverflow.com/a/3758880/17173324
        long absolute = bytes == Long.MIN_VALUE ? Long.MAX_VALUE : Math.abs(bytes);
        if (absolute < UNIT) return bytes + " B";
        long value = absolute;
        CharacterIterator ci = new StringCharacterIterator("KMGTPE");
        for (int i = 40; i >= 0 && absolute > 0xfffccccccccccccL >> i; i -= 10) {
            value >>= 10;
            ci.next();
        }
        value *= Long.signum(bytes);
        return String.format("%.2f %cB", value / UNIT, ci.current());
    }
}
