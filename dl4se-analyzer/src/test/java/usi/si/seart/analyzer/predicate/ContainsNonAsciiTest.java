package usi.si.seart.analyzer.predicate;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.treesitter.Tree;

import java.io.UnsupportedEncodingException;

class ContainsNonAsciiTest extends PredicateTest {

    @Test
    void containsNonAsciiTest() {
        Predicate predicate = new ContainsNonAscii(() -> bytes_1);
        Boolean result = predicate.test(tree.getRootNode());
        Assertions.assertNotNull(result);
        Assertions.assertTrue(result);
    }

    @Test
    @SneakyThrows(UnsupportedEncodingException.class)
    void containsOnlyAsciiTest() {
        Predicate predicate = new ContainsNonAscii(() -> bytes_2);
        Tree tree = parser.parseString(input_2);
        Boolean result = predicate.test(tree.getRootNode());
        Assertions.assertNotNull(result);
        Assertions.assertFalse(result);
    }
}