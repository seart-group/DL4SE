package usi.si.seart.analyzer.util;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class TupleTest {

    @Test
    void invertTest() {
        Tuple<Integer, String> tuple = Tuple.of(1, "hello!");
        Tuple<String, Integer> elput = Tuple.of("hello!", 1);
        Assertions.assertEquals(elput, tuple.invert());
    }
}