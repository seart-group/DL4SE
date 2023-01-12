package usi.si.seart.analyzer.count;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.NodeMapper;
import usi.si.seart.analyzer.test.BaseTest;

class TokenCounterTest extends BaseTest {

    private final NodeMapper mapper = () -> bytes_1;

    @Test
    void countTest() {
        Counter counter = new TokenCounter(mapper);
        Long actual = counter.count(tree.getRootNode());
        // Add 1 for the extra comment word
        Assertions.assertEquals(
                nodes_1.size() + 2, actual,
                "Total number of tokens should be equal to the number of input tokens including comment tokens (words)!"
        );
    }
}