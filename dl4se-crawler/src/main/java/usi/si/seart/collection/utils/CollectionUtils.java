package usi.si.seart.collection.utils;

import lombok.experimental.UtilityClass;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@UtilityClass
public class CollectionUtils {

    /**
     * Helper method used to merge two arrays into one. The resulting array contains elements from the first array,
     * followed by the elements from the second array, preserving their original order of appearance. Does not modify
     * the contents of the arrays passed as arguments.
     *
     * @param array1 The first {@link Array}.
     * @param array2 The second {@link Array}.
     * @param <T> The type of elements in both arrays.
     * @return The merged array.
     */
    @SuppressWarnings("unchecked")
    public <T> T[] merge(T[] array1, T[] array2) {
        Objects.requireNonNull(array1);
        Objects.requireNonNull(array2);
        Class<?> type = array1.getClass().getComponentType();
        T[] merged = (T[]) Array.newInstance(type, array1.length + array2.length);
        System.arraycopy(array1, 0, merged, 0, array1.length);
        System.arraycopy(array2, 0, merged, array1.length, array2.length);
        return merged;
    }

    /**
     * Helper method used for calculating the intersection of two sets: a set of shared items between both sets.
     * Although it employs mutable operations, it ensures that the original sets are not modified by working on their
     * copies.
     *
     * @param set1 The first {@link Set}.
     * @param set2 The second {@link Set}.
     * @param <T> The type of elements in both sets.
     * @return The intersection of the two sets.
     */
    public <T> Set<T> intersection(Set<T> set1, Set<T> set2) {
        Objects.requireNonNull(set1);
        Objects.requireNonNull(set2);
        Set<T> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection;
    }

    /**
     * Helper method used for calculating the difference of two sets: a set of items contained in the first set that are
     * not part of the second set. Although it employs mutable operations, it ensures that the original sets are not
     * modified by working on their copies.
     *
     * @param set1 The first {@link Set}.
     * @param set2 The second {@link Set}.
     * @param <T> The type of elements in both sets.
     * @return The difference of the first and second set.
     */
    public <T> Set<T> difference(Set<T> set1, Set<T> set2) {
        Objects.requireNonNull(set1);
        Objects.requireNonNull(set2);
        Set<T> difference = new HashSet<>(set1);
        difference.removeAll(set2);
        return difference;
    }

    /**
     * Helper method used for retrieving all values contained in a {@code Map}. If the key does not map to any value,
     * then it is simply ignored. The order of value retrieval depends on the access order of the {@code Collection}
     * containing the keys.
     *
     * @param map A {@code Map}.
     * @param keys A {@code Collection} of keys.
     * @param <K> The key type.
     * @param <V> The value type.
     * @return The {@code Set} of values that are mapped my the keys.
     */
    public <K, V> Set<V> getAllValuesFrom(Map<K, V> map, Collection<K> keys) {
        Objects.requireNonNull(map);
        Objects.requireNonNull(keys);
        Set<V> values = new HashSet<>(keys.size());
        for (K key: keys) {
            V value = map.get(key);
            if (value != null) values.add(value);
        }
        return values;
    }
}
