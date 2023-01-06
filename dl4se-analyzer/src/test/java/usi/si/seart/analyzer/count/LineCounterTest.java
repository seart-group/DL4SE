package usi.si.seart.analyzer.count;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import usi.si.seart.analyzer.test.BaseTest;

class LineCounterTest extends BaseTest {

    @Test
    void countTest() {
        Counter counter = new LineCounter();
        Long actual = counter.count(tree.getRootNode());
        Assertions.assertEquals(
                input_1.lines().count(), actual,
                "Total number of lines should be equal to the number of lines reported by String method"
        );
    }
}