package usi.si.seart.collection.utils;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

class CollectionUtilsTest {

    @Test
    void intersectionTest() {
        Set<Integer> ints1 = Set.of(1, 2, 3, 4, 5, 6);
        Set<Integer> ints2 = Set.of(4, 5, 6, 7, 8, 9);
        Assertions.assertEquals(Set.of(4, 5, 6), CollectionUtils.intersection(ints1, ints2));

        Set<String> strs1 = Set.of("this", "is", "a", "intersection", "test");
        Set<String> strs2 = Set.of("this", "is", "another", "test");
        Assertions.assertEquals(Set.of("this", "is", "test"), CollectionUtils.intersection(strs1, strs2));
    }

    @Test
    void getAllKeysFromTest() {
        Map<Integer, Integer> map = Map.of(0, 5, 1, 6, 2, 7, 3, 8, 4, 9);
        Set<Integer> keys = Set.of(0, 1, 2, 3, 4);
        Set<Integer> values = Set.of(5, 6, 7, 8, 9);

        Assertions.assertEquals(values, CollectionUtils.getAllKeysFrom(map, keys));
        Assertions.assertEquals(Set.of(5, 6, 7), CollectionUtils.getAllKeysFrom(map, Set.of(0, 1, 2)));
        Assertions.assertEquals(Set.of(), CollectionUtils.getAllKeysFrom(map, Set.of(5, 6, 7)));
    }
}