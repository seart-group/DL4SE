package usi.si.seart.analyzer.count;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.BaseTest;
import usi.si.seart.treesitter.Node;

class CharacterCounterTest extends BaseTest {

    @Test
    void countRootTest(){
        Counter counter = new CharacterCounter(getNodeMapper());
        Long actual = counter.count(tree.getRootNode());
        // Remove 2 for the space in string and comment
        Assertions.assertEquals(
                getJoinedTokens().length() - 2, actual,
                "Total number of characters should be equal to the joined tree string without spaces!"
        );
    }

    @Test
    void countChildrenTest() {
        Counter counter = new CharacterCounter(getNodeMapper());
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        Long actual = counter.count(package_declaration, class_declaration);
        Assertions.assertEquals(getJoinedTokens().length() - 2, actual);
    }
}