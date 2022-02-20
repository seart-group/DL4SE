package usi.si.seart.collection.utils;

import lombok.experimental.UtilityClass;

import java.util.HashSet;
import java.util.Set;

@UtilityClass
public class SetUtils {

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
        Set<T> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        return intersection;
    }
}
