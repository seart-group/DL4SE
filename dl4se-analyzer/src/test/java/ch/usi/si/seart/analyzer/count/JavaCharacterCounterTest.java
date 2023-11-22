package ch.usi.si.seart.analyzer.count;

import ch.usi.si.seart.analyzer.JavaBaseTest;
import ch.usi.si.seart.treesitter.Node;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

class JavaCharacterCounterTest extends JavaBaseTest {

    private static final String message = "Total number of characters should be equal to the joined tree string without spaces!";

    @Test
    void countEmptyTest() {
        Counter counter = new CharacterCounter();
        Assertions.assertEquals(0, counter.count());
        Assertions.assertEquals(0, counter.count(new HashSet<>()));
    }

    @Test
    void countRootTest(){
        Counter counter = new CharacterCounter();
        Long actual = counter.count(tree.getRootNode());
        // Remove 2 for the space in string and comment
        Assertions.assertEquals(getJoinedTokens().length() - 2, actual, message);
    }

    @Test
    void countChildrenTest() {
        Counter counter = new CharacterCounter();
        Node root = tree.getRootNode();
        Node package_declaration = root.getChild(0);
        Node class_declaration = root.getChild(1);
        Long actual = counter.count(package_declaration, class_declaration);
        // Remove 2 for the space in string and comment
        Assertions.assertEquals(getJoinedTokens().length() - 2, actual, message);
    }
}
