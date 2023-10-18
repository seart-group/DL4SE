package ch.usi.si.seart.analyzer.count;

import ch.usi.si.seart.analyzer.test.JavaBaseTest;
import ch.usi.si.seart.treesitter.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

class JavaTokenCounterTest extends JavaBaseTest {

    private static final String message = "Total number of tokens should be equal to the number of input tokens including comment tokens (words)!";

    @Test
    void countEmptyTest() {
        Counter counter = new JavaTokenCounter();
        Assertions.assertEquals(0, counter.count());
        Assertions.assertEquals(0, counter.count(new HashSet<>()));
    }

    @Test
    void countRootTest() {
        Counter counter = new JavaTokenCounter();
        Long actual = counter.count(tree.getRootNode());
        // Add 2 for the individual comment words
        Assertions.assertEquals(getNodes().size() + 2, actual, message);
    }

    @Test
    void countChildrenTest() {
        Counter counter = new JavaTokenCounter();
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        Long actual = counter.count(package_declaration, class_declaration);
        // Add 2 for the individual comment words
        Assertions.assertEquals(getNodes().size() + 2, actual, message);
    }
}
