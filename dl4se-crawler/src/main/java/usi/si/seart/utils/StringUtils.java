package usi.si.seart.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@UtilityClass
public class StringUtils {

    /**
     * Simple implementation of the SHA-256 algorithm.
     *
     * @param input An input {@link String} of arbitrary length.
     * @return The SHA-256 hashing algorithm result.
     * @apiNote We suppress throws of {@link NoSuchAlgorithmException}.
     * @see <a href="https://stackoverflow.com/a/11009612/17173324">Stack Overflow Thread</a>
     * @author user1452273
     * @author dabico
     */
    @SneakyThrows({NoSuchAlgorithmException.class})
    public static String sha256(String input) {
        Objects.requireNonNull(input);
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * Wrapper function for RegEx used to check if an input string contains any non-ASCII characters.
     *
     * @param input The input {@link String} to check against.
     * @return Whether it contains non-ASCII characters.
     * @see <a href="https://stackoverflow.com/a/3585284/17173324">Stack Overflow Thread</a>
     * @author Arne Deutsch
     * @author dabico
     */
    public static boolean containsNonAscii(String input) {
        Objects.requireNonNull(input);
        return !input.matches("\\A\\p{ASCII}*\\z");
    }
}
