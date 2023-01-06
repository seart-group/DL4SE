package usi.si.seart.analyzer.count;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.BaseTest;

class TokenCounterTest extends BaseTest {

    @Test
    void countTest() {
        Counter counter = new TokenCounter(bytes_1);
        Long actual = counter.count(tree.getRootNode());
        // Add 1 for the extra comment word
        Assertions.assertEquals(
                nodes_1.size() + 2, actual,
                "Total number of tokens should be equal to the number of input tokens including comment tokens (words)!"
        );
    }

    @Test
    void isNotReadyTest() {
        Counter counter = new TokenCounter();
        Long actual = counter.count(tree.getRootNode());
        Assertions.assertEquals(
                0L, actual, "Total number of tokens should be zero if backing byte array was not set!"
        );
    }
}