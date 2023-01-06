package usi.si.seart.analyzer.hash;

import lombok.SneakyThrows;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class SHA256Hasher implements Hasher {

    public static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_16LE;

    protected final MessageDigest md;

    @SneakyThrows(NoSuchAlgorithmException.class)
    protected SHA256Hasher() {
        this.md = MessageDigest.getInstance("SHA-256");
    }

    static String bytesToHex(byte[] bytes) {
        StringBuilder hexBuilder = new StringBuilder(2 * bytes.length);
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexBuilder.append('0');
            hexBuilder.append(hex);
        }
        return hexBuilder.toString();
    }
}
