package usi.si.seart.analyzer.hash;

import lombok.SneakyThrows;
import usi.si.seart.analyzer.test.BaseTest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public abstract class HasherTest extends BaseTest {

    @Override
    protected List<String> getTokens() {
        return List.of(
            "package",
            "ch",
            ".",
            "usi",
            ".",
            "si",
            ";",
            "public",
            "class",
            "Main",
            "{",
            "public",
            "static",
            "void",
            "main",
            "(",
            "String",
            "[]",
            "args",
            ")",
            "{",
            "System",
            ".",
            "out",
            ".",
            "println",
            "(",
            "\"Hello, World!\"",
            ")",
            ";",
            "}",
            "}"
        );
    }

    @Override
    protected List<String> getNodes() {
        return List.of(
            "package",
            "identifier",
            ".",
            "identifier",
            ".",
            "identifier",
            ";",
            "public",
            "class",
            "identifier",
            "{",
            "public",
            "static",
            "void_type",
            "identifier",
            "(",
            "type_identifier",
            "[]",
            "identifier",
            ")",
            "{",
            "identifier",
            ".",
            "identifier",
            ".",
            "identifier",
            "(",
            "string_literal",
            ")",
            ";",
            "}",
            "}"
        );
    }

    @SneakyThrows({NoSuchAlgorithmException.class})
    protected String sha256(String input) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(SHA256Hasher.DEFAULT_CHARSET));
        return SHA256Hasher.bytesToHex(hash);
    }
}
