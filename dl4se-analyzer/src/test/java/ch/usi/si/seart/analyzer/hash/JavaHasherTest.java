package ch.usi.si.seart.analyzer.hash;

import ch.usi.si.seart.analyzer.JavaBaseTest;
import lombok.SneakyThrows;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public abstract class JavaHasherTest extends JavaBaseTest {

    // https://crypto.stackexchange.com/a/26135
    protected static final String EMPTY = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855";

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

    @SneakyThrows(NoSuchAlgorithmException.class)
    protected String sha256(String input) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] bytes = input.getBytes(getCharset());
        byte[] hash = md.digest(bytes);
        StringBuilder hexBuilder = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexBuilder.append('0');
            hexBuilder.append(hex);
        }
        return hexBuilder.toString();
    }
}
