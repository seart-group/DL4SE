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

class SyntaxTreeHasherTest extends HasherTest {

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

    private static final String abs =
            "packageidentifier.identifier.identifier;publicclassidentifier{publicstaticvoid_typeidentifier(type_identifier[]identifier){identifier.identifier.identifier(string_literal);}}";

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
        Hasher hasher = new SyntaxTreeHasher();
        String actual = hasher.hash(tree);
        String expected = sha256(abs);
        Assertions.assertEquals(expected, actual, "Incrementally digested hash of leaf node names should be equal to the manual digest of the flattened, abstract, typed code!");
    }

    @Test
    void idempotencyTest() {
        Hasher hasher = new SyntaxTreeHasher();
        String first = hasher.hash(tree);
        String second = hasher.hash(tree);
        Assertions.assertEquals(first, second, "Same instance should be reusable for multiple invocations!");
    }

    @Test
    @SneakyThrows(UnsupportedEncodingException.class)
    void noCommentImpactTest() {
        Tree other = parser.parseString(otherInput);
        Hasher hasher = new SyntaxTreeHasher();
        String first = hasher.hash(tree);
        String second = hasher.hash(other);
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