package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.JavaBaseTest;

import java.util.HashSet;

class JavaLineCounterTest extends JavaBaseTest {

    private static final String message = "Total number of lines should be equal to the number of lines reported by the `lines()` method";

    @Test
    void countEmptyTest() {
        Counter counter = new LineCounter();
        Assertions.assertEquals(0, counter.count());
        Assertions.assertEquals(0, counter.count(new HashSet<>()));
    }

    @Test
    void countRootTest() {
        Counter counter = new LineCounter();
        Long actual = counter.count(tree.getRootNode());
        Assertions.assertEquals(getInput().lines().count(), actual, message);
    }

    @Test
    void countChildrenTest() {
        Counter counter = new LineCounter();
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        Long actual = counter.count(package_declaration, class_declaration);
        // Subtract one for the lost space between package and class
        Assertions.assertEquals(getInput().lines().count() - 1, actual, message);
    }
}