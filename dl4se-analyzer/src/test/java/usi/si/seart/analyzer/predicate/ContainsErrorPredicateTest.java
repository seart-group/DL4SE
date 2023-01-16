package usi.si.seart.analyzer.predicate;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.treesitter.Language;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Parser;
import usi.si.seart.treesitter.Tree;

import java.io.UnsupportedEncodingException;
import java.util.function.Predicate;

class ContainsErrorPredicateTest extends PredicateTest {

    @Test
    @SneakyThrows(UnsupportedEncodingException.class)
    void containsErrorTest() {
        @Cleanup Parser parser = new Parser(Language.C);
        Tree error = parser.parseString(input_1);
        Node root = error.getRootNode();
        Predicate<Node> predicate = new ContainsErrorPredicate();
        boolean result = predicate.test(root);
        Assertions.assertTrue(result);
    }

    @Test
    void containsNoErrorTest() {
        Predicate<Node> predicate = new ContainsErrorPredicate();
        boolean result = predicate.test(tree.getRootNode());
        Assertions.assertFalse(result);
    }
}