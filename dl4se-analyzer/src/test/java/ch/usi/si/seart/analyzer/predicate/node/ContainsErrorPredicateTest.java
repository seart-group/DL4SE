package ch.usi.si.seart.analyzer.predicate.node;

import ch.usi.si.seart.treesitter.Language;
import ch.usi.si.seart.treesitter.Node;
import ch.usi.si.seart.treesitter.Parser;
import ch.usi.si.seart.treesitter.Tree;
import lombok.Cleanup;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

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
    void containsErrorTest() {
        @Cleanup Parser parser = Parser.getFor(Language.C);
        Tree error = parser.parse(getInput());
        Node root = error.getRootNode();
        NodePredicate predicate = new ContainsErrorPredicate();
        boolean result = predicate.test(root);
        Assertions.assertTrue(result);
    }

    @Test
    void anyContainsErrorTest() {
        @Cleanup Parser parser = Parser.getFor(Language.C);
        Tree error = parser.parse(getInput());
        Node root = error.getRootNode();
        Node declaration = root.getChild(0);
        Node function_definition = root.getChild(1);
        NodePredicate predicate = new ContainsErrorPredicate();
        boolean result = predicate.test(declaration, function_definition);
        Assertions.assertTrue(result);
    }
}
