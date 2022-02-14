package usi.si.seart.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@UtilityClass
public class StringUtils {

    /**
     * Simple implementation of the SHA-256 algorithm.
     *
     * @param input An input {@link String} of arbitrary length.
     * @return The SHA-256 hashing algorithm result.
     * @apiNote We suppress {@link NoSuchAlgorithmException}, as it will never occur with the fixed input.
     * @see <a href="https://stackoverflow.com/a/11009612/17173324">Stack Overflow Thread</a>
     * @author user1452273
     * @author dabico
     */
    @SneakyThrows({NoSuchAlgorithmException.class})
    public static String sha256(String input) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            final String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1)
                hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}
