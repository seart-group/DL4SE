package usi.si.seart.collection.utils;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class CollectionUtilsTest {

    Set<Integer> ints1 = Set.of(1, 2, 3, 4, 5, 6);
    Set<Integer> ints2 = Set.of(4, 5, 6, 7, 8, 9);
    Set<String> strs1 = Set.of("this", "is", "a", "utils", "test");
    Set<String> strs2 = Set.of("this", "is", "another", "test");

    @Test
    void mergeTest() {
        Integer[] empty = new Integer[]{};
        Integer[] arr1 = new Integer[] {1, 2, 3};
        Integer[] arr2 = new Integer[] {4, 5, 6};
        Assertions.assertArrayEquals(empty, CollectionUtils.merge(empty, empty));
        Assertions.assertArrayEquals(arr1, CollectionUtils.merge(empty, arr1));
        Assertions.assertArrayEquals(arr1, CollectionUtils.merge(arr1, empty));
        Assertions.assertArrayEquals(new Integer[] {1, 2, 3, 4, 5, 6}, CollectionUtils.merge(arr1, arr2));
        Assertions.assertArrayEquals(new Integer[] {1, 2, 3, 1, 2, 3}, CollectionUtils.merge(arr1, arr1));
    }

    @Test
    void intersectionTest() {
        Assertions.assertEquals(Set.of(4, 5, 6), CollectionUtils.intersection(ints1, ints2));
        Assertions.assertEquals(Set.of("this", "is", "test"), CollectionUtils.intersection(strs1, strs2));
    }

    @Test
    void differenceTest() {
        Assertions.assertEquals(Set.of(1, 2, 3), CollectionUtils.difference(ints1, ints2));
        Assertions.assertEquals(Set.of("a", "utils"), CollectionUtils.difference(strs1, strs2));
    }

    @Test
    void getAllKeysFromTest() {
        Map<Integer, Integer> map = Map.of(0, 5, 1, 6, 2, 7, 3, 8, 4, 9);
        Set<Integer> keys = Set.of(0, 1, 2, 3, 4);

        Assertions.assertEquals(Set.of(5, 6, 7, 8, 9), CollectionUtils.getAllValuesFrom(map, keys));
        Assertions.assertEquals(Set.of(5, 6, 7), CollectionUtils.getAllValuesFrom(map, Set.of(0, 1, 2)));
        Assertions.assertEquals(Set.of(), CollectionUtils.getAllValuesFrom(map, Set.of(5, 6, 7)));
    }
}