package usi.si.seart.analyzer.count;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.BaseTest;
import usi.si.seart.treesitter.Node;

class TokenCounterTest extends BaseTest {

    @Test
    void countRootTest() {
        Counter counter = new TokenCounter(getNodeMapper());
        Long actual = counter.count(tree.getRootNode());
        // Add 1 for the extra comment word
        Assertions.assertEquals(
                getNodes().size() + 2, actual,
                "Total number of tokens should be equal to the number of input tokens including comment tokens (words)!"
        );
    }

    @Test
    void countChildrenTest() {
        Counter counter = new TokenCounter(getNodeMapper());
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        Long actual = counter.count(package_declaration, class_declaration);
        Assertions.assertEquals(getNodes().size() + 2, actual);
    }
}