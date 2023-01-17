package usi.si.seart.analyzer.count;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.BaseTest;

class CharacterCounterTest extends BaseTest {

    @Test
    void countTest(){
        Counter counter = new CharacterCounter(getNodeMapper());
        Long actual = counter.count(tree.getRootNode());
        // Remove 2 for the space in string and comment
        Assertions.assertEquals(
                getJoinedTokens().length() - 2, actual,
                "Total number of characters should be equal to the joined tree string without spaces!"
        );
    }
}