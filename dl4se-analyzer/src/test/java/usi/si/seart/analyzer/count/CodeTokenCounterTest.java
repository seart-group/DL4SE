package usi.si.seart.analyzer.count;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.BaseTest;
import usi.si.seart.treesitter.Node;

class CodeTokenCounterTest extends BaseTest {

    @Test
    void countRootTest() {
        Counter counter = new CodeTokenCounter();
        Long actual = counter.count(tree.getRootNode());
        // Remove 1 for the comment node
        Assertions.assertEquals(
                getNodes().size() - 1, actual,
                "Total number of code tokens should be equal to the number of input tokens without the comments!"
        );
    }

    @Test
    void countChildrenTest() {
        Counter counter = new CodeTokenCounter();
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        Long actual = counter.count(package_declaration, class_declaration);
        Assertions.assertEquals(getNodes().size() - 1, actual, actual);
    }
}