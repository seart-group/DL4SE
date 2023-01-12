package usi.si.seart.analyzer.query;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public interface Validated {

    Pattern CAPTURE_PATTERN = Pattern.compile("@[A-Za-z0-9_]+");

    default void validate(String sExpr, String message) {
        int count = 0;
        Matcher matcher = CAPTURE_PATTERN.matcher(sExpr);
        while (matcher.find()) count++;
        if (!isPastThreshold(count))
            throw new IllegalArgumentException(message);
    }

    default boolean isPastThreshold(int count) {
        return count >= 0;
    }
}
