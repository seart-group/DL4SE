package usi.si.seart.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestClass {

    @Test
    public void testMax() {
        Assertions.assertEquals(5, Math.max(3, 5));
    }

    @Test
    public void minTest() {
        Assertions.assertEquals(3, Math.min(3, 5));
    }
}
