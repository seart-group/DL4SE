package ch.usi.si.seart.analyzer.count;

import ch.usi.si.seart.analyzer.JavaBaseTest;
import ch.usi.si.seart.treesitter.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

class JavaCodeTokenCounterTest extends JavaBaseTest {

    private static final String message = "Total number of code tokens should be equal to the number of input tokens without the comments!";

    @Test
    void countEmptyTest() {
        Counter counter = new CodeTokenCounter();
        Assertions.assertEquals(0, counter.count());
        Assertions.assertEquals(0, counter.count(new HashSet<>()));
    }

    @Test
    void countRootTest() {
        Counter counter = new CodeTokenCounter();
        Long actual = counter.count(tree.getRootNode());
        // Remove 1 for the comment node
        Assertions.assertEquals(getNodes().size() - 1, actual, message);
    }

    @Test
    void countChildrenTest() {
        Counter counter = new CodeTokenCounter();
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        Long actual = counter.count(package_declaration, class_declaration);
        // Remove 1 for the comment node
        Assertions.assertEquals(getNodes().size() - 1, actual, message);
    }
}
