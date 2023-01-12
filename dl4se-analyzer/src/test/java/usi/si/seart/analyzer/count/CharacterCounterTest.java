package usi.si.seart.analyzer.count;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.NodeMapper;
import usi.si.seart.analyzer.test.BaseTest;

class CharacterCounterTest extends BaseTest {

    private final NodeMapper mapper = () -> bytes_1;

    @Test
    void countTest(){
        Counter counter = new CharacterCounter(mapper);
        Long actual = counter.count(tree.getRootNode());
        // Remove 2 for the space in string and comment
        Assertions.assertEquals(
                tokens_joined_1.length() - 2,
                actual,
                "Total number of characters should be equal to the joined tree string without spaces!"
        );
    }
}