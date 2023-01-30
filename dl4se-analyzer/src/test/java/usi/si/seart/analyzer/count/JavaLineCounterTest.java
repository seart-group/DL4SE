package usi.si.seart.analyzer.count;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.JavaBaseTest;
import usi.si.seart.treesitter.Node;

import java.util.HashSet;

class JavaLineCounterTest extends JavaBaseTest {

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
        Assertions.assertEquals(
                getInput().lines().count(), actual,
                "Total number of lines should be equal to the number of lines reported by String method"
        );
    }

    @Test
    void countChildrenTest() {
        Counter counter = new LineCounter();
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        Long actual = counter.count(package_declaration, class_declaration);
        // Subtract one for the lost space between package and class
        Assertions.assertEquals(getInput().lines().count() - 1, actual);
    }
}