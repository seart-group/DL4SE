package usi.si.seart.analyzer.hash;

import ch.usi.si.seart.treesitter.Tree;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;

class JavaContentHasherTest extends JavaHasherTest {

    @Test
    void emptyTest() {
        Hasher hasher = new ContentHasher(getNodeMapper());
        Assertions.assertEquals(empty, hasher.hash());
        Assertions.assertEquals(empty, hasher.hash(new HashSet<>()));
    }

    @Test
    void hashTest() {
        Hasher hasher = new ContentHasher(getNodeMapper());
        String actual = hasher.hash(tree.getRootNode());
        String expected = sha256(getJoinedTokens());
        Assertions.assertEquals(expected, actual, "Incrementally digested tree hash should be equal to the flat code manual digest!");
    }

    @Test
    void idempotencyTest() {
        Hasher hasher = new ContentHasher(getNodeMapper());
        String first = hasher.hash(tree.getRootNode());
        String second = hasher.hash(tree.getRootNode());
        Assertions.assertEquals(first, second, "Same instance should be reusable for multiple invocations!");
    }

    @Test
    @SneakyThrows(UnsupportedEncodingException.class)
    void noCommentImpactTest() {
        String input_2 =
                "package ch.usi.si;\n" +
                "\n" +
                "public class Main {\n" +
                "    public static void main(String[] args) {\n" +
                "        /*\n" +
                "         * Block comment\n" +
                "         * on multiple lines\n" +
                "         */\n" +
                "        System.out.println(\"Hello, World!\");\n" +
                "    }\n" +
                "}";

        byte[] bytes_2 = input_2.getBytes(StandardCharsets.UTF_16LE);
        Tree other = parser.parseString(input_2);
        ContentHasher hasher_1 = new ContentHasher(getNodeMapper());
        ContentHasher hasher_2 = new ContentHasher(() -> bytes_2);
        String first = hasher_1.hash(tree.getRootNode());
        String second = hasher_2.hash(other.getRootNode());
        Assertions.assertEquals(first, second, "Comments should not impact the hashing result!");
    }
}