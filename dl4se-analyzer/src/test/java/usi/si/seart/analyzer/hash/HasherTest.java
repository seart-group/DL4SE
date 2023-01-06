package usi.si.seart.analyzer.hash;

import lombok.SneakyThrows;
import usi.si.seart.analyzer.test.BaseTest;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public abstract class HasherTest extends BaseTest {

    protected final List<String> tokens_1 = List.of(
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

    protected final String tokens_joined_1 = String.join("", tokens_1);

    protected final List<String> nodes_1 = List.of(
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

    protected final String nodes_joined_1 = String.join("", nodes_1);

    @SneakyThrows({NoSuchAlgorithmException.class})
    protected String sha256(String input) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(input.getBytes(SHA256Hasher.DEFAULT_CHARSET));
        return SHA256Hasher.bytesToHex(hash);
    }
}
