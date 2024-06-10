package ch.usi.si.seart.analyzer.hash;

import ch.usi.si.seart.treesitter.Tree;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

class JavaContentHasherTest extends JavaHasherTest {

    @Test
    void emptyTest() {
        Hasher hasher = new ContentHasher();
        Assertions.assertEquals(EMPTY, hasher.hash());
        Assertions.assertEquals(EMPTY, hasher.hash(new HashSet<>()));
    }

    @Test
    void hashTest() {
        Hasher hasher = new ContentHasher();
        String actual = hasher.hash(tree.getRootNode());
        String expected = sha256(getJoinedTokens());
        Assertions.assertEquals(expected, actual, "Incrementally digested tree hash should be equal to the flat code manual digest!");
    }

    @Test
    void idempotencyTest() {
        Hasher hasher = new ContentHasher();
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
        ContentHasher hasher_1 = new ContentHasher();
        ContentHasher hasher_2 = new ContentHasher();
        String actual = hasher_1.hash(tree.getRootNode());
        String expected = hasher_2.hash(other.getRootNode());
        Assertions.assertEquals(expected, actual, "Comments should not impact the hashing result!");
    }
}
