package usi.si.seart.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@UtilityClass
public class StringUtils {

    /**
     * Simple implementation of the SHA-256 algorithm.
     *
     * @param input An input {@code String} of arbitrary length.
     * @return A 64-character {@code String} representing the hashing algorithm result.
     * @implNote We suppress throws of {@link NoSuchAlgorithmException}.
     * @see <a href="https://www.baeldung.com/sha-256-hashing-java#message-digest">Baeldung Guide</a>
     * @author dabico
     */
    @SneakyThrows({NoSuchAlgorithmException.class})
    public String sha256(String input) {
        Objects.requireNonNull(input);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Used to check if an input {@code String} contains any non-ASCII characters.
     *
     * @param input The input {@code String} to check against.
     * @return {@code true} if it contains any non-ASCII characters, {@code false} otherwise.
     * @author dabico
     */
    public boolean containsNonAscii(String input) {
        Objects.requireNonNull(input);
        return input.chars().anyMatch(ch -> ch > 127);
    }

    /**
     * Used to normalize white spaces in a {@code String}. For a passed input we replace all consecutive occurrences of
     * whitespace characters as defined by {@link Character#isWhitespace(char) Character.isWhiteSpace} with a single
     * space character. Before returning, the resulting {@code String} is also stripped of any leading or trailing
     * whitespaces.
     *
     * @param input An input {@code String}.
     * @return The space-normalized input.
     * @author dabico
     */
    public String normalizeSpace(String input) {
        Objects.requireNonNull(input);
        if (input.isBlank()) return "";

        StringBuilder builder = new StringBuilder(input.length());
        boolean lastWasWhitespace = false;
        for (int i = 0; i < input.length(); i++) {
            char current = input.charAt(i);
            if (Character.isWhitespace(current)) {
                if (!lastWasWhitespace) {
                    lastWasWhitespace = true;
                    builder.append(' ');
                }
            } else {
                lastWasWhitespace = false;
                builder.append(current);
            }
        }

        return builder.toString().trim();
    }

    /**
     * Used to read any arbitrary {@code InputStream} into a {@code String}.
     *
     * @param inputStream An {@code InputStream}.
     * @return The stream contents as a {@code String}.
     * @apiNote Intended to be used for processing the STD/ERR output of a {@link java.lang.Process Process}.
     * @implNote We suppress throws of {@link IOException}.
     * @author dabico
     * @see <a href="https://www.baeldung.com/convert-input-stream-to-string#converting-with-java-9---inputstreamreadallbytes">Baeldung Guide</a>
     */
    @SneakyThrows({IOException.class})
    public String fromInputStream(InputStream inputStream) {
        Objects.requireNonNull(inputStream);
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }
}
