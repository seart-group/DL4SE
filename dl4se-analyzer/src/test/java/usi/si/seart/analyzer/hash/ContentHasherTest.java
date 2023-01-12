package usi.si.seart.analyzer.hash;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.NodeMapper;
import usi.si.seart.treesitter.Tree;

import java.io.UnsupportedEncodingException;

class ContentHasherTest extends HasherTest {

    private final NodeMapper mapper = () -> bytes_1;

    @Test
    void hashTest() {
        Hasher hasher = new ContentHasher(mapper);
        String actual = hasher.hash(tree.getRootNode());
        String expected = sha256(tokens_joined_1);
        Assertions.assertEquals(expected, actual, "Incrementally digested tree hash should be equal to the flat code manual digest!");
    }

    @Test
    void idempotencyTest() {
        Hasher hasher = new ContentHasher(mapper);
        String first = hasher.hash(tree.getRootNode());
        String second = hasher.hash(tree.getRootNode());
        Assertions.assertEquals(first, second, "Same instance should be reusable for multiple invocations!");
    }

    @Test
    @SneakyThrows(UnsupportedEncodingException.class)
    void noCommentImpactTest() {
        Tree other = parser.parseString(input_2);
        ContentHasher hasher_1 = new ContentHasher(() -> bytes_1);
        ContentHasher hasher_2 = new ContentHasher(() -> bytes_2);
        String first = hasher_1.hash(tree.getRootNode());
        String second = hasher_2.hash(other.getRootNode());
        Assertions.assertEquals(first, second, "Comments should not impact the hashing result!");
    }
}