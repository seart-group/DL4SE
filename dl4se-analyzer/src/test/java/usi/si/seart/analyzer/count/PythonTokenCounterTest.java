package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.PythonBaseTest;

import java.util.HashSet;

class PythonTokenCounterTest extends PythonBaseTest {

    @Test
    void countEmptyTest() {
        Counter counter = new PythonTokenCounter(getNodeMapper());
        Assertions.assertEquals(0, counter.count());
        Assertions.assertEquals(0, counter.count(new HashSet<>()));
    }

    @Test
    void countRootTest() {
        Counter counter = new PythonTokenCounter(getNodeMapper());
        Long actual = counter.count(tree.getRootNode());
        // Add 2 for the individual comment words
        Assertions.assertEquals(
                getNodes().size() + 2, actual,
                "Total number of tokens should be equal to the number of input tokens including comment tokens (words)!"
        );
    }

    @Test
    void countChildrenTest() {
        Counter counter = new PythonTokenCounter(getNodeMapper());
        Node module = tree.getRootNode();
        Node function = module.getChild(0);
        Node[] children = function.getChildren().toArray(new Node[0]);
        Long actual = counter.count(children);
        // Add 2 for the individual comment words
        Assertions.assertEquals(
                getNodes().size() + 2, actual,
                "Total number of tokens should be equal to the number of input tokens including comment tokens (words)!"
        );
    }
}
