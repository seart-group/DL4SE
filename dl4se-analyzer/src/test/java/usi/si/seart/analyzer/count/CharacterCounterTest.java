package usi.si.seart.analyzer.count;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.BaseTest;

class CharacterCounterTest extends BaseTest {

    @Test
    void countTest(){
        Counter counter = new CharacterCounter(bytes_1);
        Long actual = counter.count(tree.getRootNode());
        // Remove 2 for the space in string and comment
        Assertions.assertEquals(
                tokens_joined_1.length() - 2,
                actual,
                "Total number of characters should be equal to the joined tree string without spaces!"
        );
    }

    @Test
    void isNotReadyTest() {
        Counter counter = new CharacterCounter();
        Long actual = counter.count(tree.getRootNode());
        Assertions.assertEquals(
                0L, actual, "Total number of characters should be zero if backing byte array was not set!"
        );
    }
}