package usi.si.seart.analyzer.count;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.BaseTest;

class CodeTokenCounterTest extends BaseTest {

    @Test
    void countTest() {
        Counter counter = new CodeTokenCounter();
        Long actual = counter.count(tree.getRootNode());
        // Remove 1 for the comment node
        Assertions.assertEquals(
                nodes_1.size() - 1, actual,
                "Total number of code tokens should be equal to the number of input tokens without the comments!"
        );
    }
}