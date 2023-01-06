package usi.si.seart.analyzer.hash;

import lombok.SneakyThrows;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import usi.si.seart.treesitter.Language;
import usi.si.seart.treesitter.Parser;
import usi.si.seart.treesitter.Tree;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

class ContentHasherTest extends HasherTest {

    private static Parser parser;

    private Tree tree;

    private static final String input =
            "package ch.usi.si;\n" +
            "\n" +
            "public class Main {\n" +
            "\tpublic static void main(String[] args) {\n" +
            "\t\t//line comment\n" +
            "\t\tSystem.out.println(\"Hello, World!\");\n" +
            "\t}\n" +
            "}";

    private static final String otherInput =
            "package ch.usi.si;\n" +
            "\n" +
            "public class Main {\n" +
            "\tpublic static void main(String[] args) {\n" +
            "\t\t/*\n" +
            "\t\t * Block comment\n" +
            "\t\t * on multiple lines\n" +
            "\t\t */\n" +
            "\t\tSystem.out.println(\"Hello, World!\");\n" +
            "\t}\n" +
            "}";

    private static final String inputFlat =
            "packagech.usi.si;publicclassMain{publicstaticvoidmain(String[]args){System.out.println(\"Hello, World!\");}}";

    private static final byte[] bytes = input.getBytes(StandardCharsets.UTF_16LE);
    private static final byte[] otherBytes = otherInput.getBytes(StandardCharsets.UTF_16LE);

    @BeforeAll
    static void beforeAll() {
        parser = new Parser();
        parser.setLanguage(Language.JAVA);
    }

    @BeforeEach
    @SneakyThrows(UnsupportedEncodingException.class)
    void setUp() {
        tree = parser.parseString(input);
    }

    @Test
    void hashTest() {
        Hasher hasher = new ContentHasher(bytes);
        String actual = hasher.hash(tree.getRootNode());
        String expected = sha256(inputFlat);
        Assertions.assertEquals(expected, actual, "Incrementally digested tree hash should be equal to the flat code manual digest!");
    }

    @Test
    void idempotencyTest() {
        Hasher hasher = new ContentHasher(bytes);
        String first = hasher.hash(tree.getRootNode());
        String second = hasher.hash(tree.getRootNode());
        Assertions.assertEquals(first, second, "Same instance should be reusable for multiple invocations!");
    }

    @Test
    @SneakyThrows(UnsupportedEncodingException.class)
    void noCommentImpactTest() {
        Tree other = parser.parseString(otherInput);
        ContentHasher hasher = new ContentHasher();
        hasher.setBytes(bytes);
        String first = hasher.hash(tree.getRootNode());
        hasher.setBytes(otherBytes);
        String second = hasher.hash(other.getRootNode());
        Assertions.assertEquals(first, second, "Comments should not impact the hashing result!");
    }

    @AfterEach
    void tearDown() {
        tree.close();
        tree = null;
    }

    @AfterAll
    static void afterAll() {
        parser.close();
    }
}