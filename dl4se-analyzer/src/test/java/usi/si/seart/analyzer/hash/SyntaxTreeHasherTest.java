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

    @BeforeAll
    static void beforeAll() {
        parser = new Parser();
        parser.setLanguage(Language.JAVA);
    }

    @BeforeEach
    @SneakyThrows(UnsupportedEncodingException.class)
    void setUp() {
        tree = parser.parseString(input_1);
    }

    @Test
    void hashTest() {
        Hasher hasher = new SyntaxTreeHasher();
        String actual = hasher.hash(tree.getRootNode());
        String expected = sha256(nodes_joined_1);
        Assertions.assertEquals(expected, actual, "Incrementally digested hash of leaf node names should be equal to the manual digest of the flattened, abstract, typed code!");
    }

    @Test
    void idempotencyTest() {
        Hasher hasher = new SyntaxTreeHasher();
        String first = hasher.hash(tree.getRootNode());
        String second = hasher.hash(tree.getRootNode());
        Assertions.assertEquals(first, second, "Same instance should be reusable for multiple invocations!");
    }

    @Test
    @SneakyThrows(UnsupportedEncodingException.class)
    void noCommentImpactTest() {
        Tree other = parser.parseString(input_2);
        Hasher hasher = new SyntaxTreeHasher();
        String first = hasher.hash(tree.getRootNode());
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