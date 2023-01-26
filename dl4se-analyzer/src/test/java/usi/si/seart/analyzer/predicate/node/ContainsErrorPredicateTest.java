package usi.si.seart.analyzer.predicate.node;

import lombok.Cleanup;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.treesitter.Language;
import usi.si.seart.treesitter.Node;
import usi.si.seart.treesitter.Parser;
import usi.si.seart.treesitter.Tree;

import java.io.UnsupportedEncodingException;
import java.util.HashSet;

class ContainsErrorPredicateTest extends PredicateTest {

    @Test
    void emptyInputTest() {
        NodePredicate predicate = new ContainsErrorPredicate();
        Assertions.assertFalse(predicate.test());
        Assertions.assertFalse(predicate.test(new HashSet<>()));
    }

    @Test
    void containsNoErrorTest() {
        NodePredicate predicate = new ContainsErrorPredicate();
        boolean result = predicate.test(tree.getRootNode());
        Assertions.assertFalse(result);
    }

    @Test
    @SneakyThrows(UnsupportedEncodingException.class)
    void containsErrorTest() {
        @Cleanup Parser parser = new Parser(Language.C);
        Tree error = parser.parseString(getInput());
        Node root = error.getRootNode();
        NodePredicate predicate = new ContainsErrorPredicate();
        boolean result = predicate.test(root);
        Assertions.assertTrue(result);
    }

    @Test
    @SneakyThrows(UnsupportedEncodingException.class)
    void anyContainsErrorTest() {
        @Cleanup Parser parser = new Parser(Language.C);
        Tree error = parser.parseString(getInput());
        Node root = error.getRootNode();
        Node declaration = root.getChild(0);
        Node function_definition = root.getChild(1);
        NodePredicate predicate = new ContainsErrorPredicate();
        boolean result = predicate.test(declaration, function_definition);
        Assertions.assertTrue(result);
    }
}