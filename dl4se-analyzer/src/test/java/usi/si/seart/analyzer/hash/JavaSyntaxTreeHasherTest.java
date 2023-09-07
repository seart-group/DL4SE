package usi.si.seart.analyzer.hash;

import ch.usi.si.seart.treesitter.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.charset.Charset;
import java.util.HashSet;

class JavaSyntaxTreeHasherTest extends JavaHasherTest {

    @Test
    void emptyTest() {
        Hasher hasher = new SyntaxTreeHasher();
        Assertions.assertEquals(EMPTY, hasher.hash());
        Assertions.assertEquals(EMPTY, hasher.hash(new HashSet<>()));
    }

    @Test
    void hashTest() {
        Hasher hasher = new SyntaxTreeHasher();
        String actual = hasher.hash(tree.getRootNode());
        String expected = sha256(getJoinedNodes());
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
        Tree other = parser.parse(input_2);
        Hasher hasher = new SyntaxTreeHasher();
        String first = hasher.hash(tree.getRootNode());
        String second = hasher.hash(other.getRootNode());
        Assertions.assertEquals(first, second, "Comments should not impact the hashing result!");
    }

    @Override
    protected Charset getCharset() {
        return Charset.defaultCharset();
    }
}