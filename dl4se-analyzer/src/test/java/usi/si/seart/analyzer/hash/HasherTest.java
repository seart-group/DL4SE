package usi.si.seart.analyzer.hash;

import lombok.SneakyThrows;
import usi.si.seart.analyzer.test.BaseTest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public abstract class HasherTest extends BaseTest {

    @SneakyThrows({NoSuchAlgorithmException.class})
    protected String sha256(String input) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(SHA256Hasher.DEFAULT_CHARSET));
        return SHA256Hasher.bytesToHex(hash);
    }
}
