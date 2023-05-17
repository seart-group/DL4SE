package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.PythonBaseTest;

import java.util.HashSet;

class PythonCodeTokenCounterTest extends PythonBaseTest {

    private static final String message = "Total number of code tokens should be equal to the number of input tokens without the comments!";

    @Test
    void countEmptyTest() {
        Counter counter = new PythonCodeTokenCounter();
        Assertions.assertEquals(0, counter.count());
        Assertions.assertEquals(0, counter.count(new HashSet<>()));
    }

    @Test
    void countRootTest() {
        Counter counter = new PythonCodeTokenCounter();
        Long actual = counter.count(tree.getRootNode());
        // Remove 1 for the comment node
        Assertions.assertEquals(getNodes().size() - 1, actual, message);
    }

    @Test
    void countChildrenTest() {
        Counter counter = new PythonCodeTokenCounter();
        Node module = tree.getRootNode();
        Node function = module.getChild(0);
        Node[] children = function.getChildren().toArray(new Node[0]);
        Long actual = counter.count(children);
        // Remove 1 for the comment node
        Assertions.assertEquals(getNodes().size() - 1, actual, message);
    }
}