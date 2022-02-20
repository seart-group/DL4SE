package usi.si.seart.collection.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class SetUtilsTest {

    @Test
    void intersectionTest() {
        Set<Integer> ints1 = Set.of(1, 2, 3, 4, 5, 6);
        Set<Integer> ints2 = Set.of(4, 5, 6, 7, 8, 9);
        Assertions.assertEquals(Set.of(4, 5, 6), SetUtils.intersection(ints1, ints2));

        Set<String> strs1 = Set.of("this", "is", "a", "intersection", "test");
        Set<String> strs2 = Set.of("this", "is", "another", "test");
        Assertions.assertEquals(Set.of("this", "is", "test"), SetUtils.intersection(strs1, strs2));
    }
}