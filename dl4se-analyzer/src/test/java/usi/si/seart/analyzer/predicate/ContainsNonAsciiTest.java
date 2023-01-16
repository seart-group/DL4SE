package usi.si.seart.analyzer.predicate;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Tree;

import java.io.UnsupportedEncodingException;
import java.util.function.Predicate;

class ContainsNonAsciiTest extends PredicateTest {

    @Test
    void containsNonAsciiTest() {
        Predicate<Node> predicate = new ContainsNonAscii(() -> bytes_1);
        boolean result = predicate.test(tree.getRootNode());
        Assertions.assertTrue(result);
    }

    @Test
    @SneakyThrows(UnsupportedEncodingException.class)
    void containsOnlyAsciiTest() {
        Predicate<Node> predicate = new ContainsNonAscii(() -> bytes_2);
        Tree tree = parser.parseString(input_2);
        boolean result = predicate.test(tree.getRootNode());
        Assertions.assertFalse(result);
    }
}