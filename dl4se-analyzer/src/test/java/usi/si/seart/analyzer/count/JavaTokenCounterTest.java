package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.JavaBaseTest;

import java.util.HashSet;

class JavaTokenCounterTest extends JavaBaseTest {

    @Test
    void countEmptyTest() {
        Counter counter = new JavaTokenCounter(getNodeMapper());
        Assertions.assertEquals(0, counter.count());
        Assertions.assertEquals(0, counter.count(new HashSet<>()));
    }

    @Test
    void countRootTest() {
        Counter counter = new JavaTokenCounter(getNodeMapper());
        Long actual = counter.count(tree.getRootNode());
        // Add 1 for the extra comment word
        Assertions.assertEquals(
                getNodes().size() + 2, actual,
                "Total number of tokens should be equal to the number of input tokens including comment tokens (words)!"
        );
    }

    @Test
    void countChildrenTest() {
        Counter counter = new JavaTokenCounter(getNodeMapper());
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        Long actual = counter.count(package_declaration, class_declaration);
        Assertions.assertEquals(getNodes().size() + 2, actual);
    }
}