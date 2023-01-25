package usi.si.seart.analyzer.util;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Map;
import java.util.Objects;

/**
 * Simple implementation of a tuple class,
 * used as a container for two values of arbitrary types.
 * Tuples are immutable, and can only be created through a static factory:
 * <pre>{@code
 *      Tuple<Integer, String> t = Tuple.of(1, "Hello!");
 * }</pre>
 * The left and right side are subsequently accessed through dedicated getters:
 * <pre>{@code
 *      int i = t.getLeft();
 *      String msg = t.getRight();
 * }</pre>
 * Instances can also be used in place of {@link Map.Entry}:
 * <pre>{@code
 *      Map<Integer, Object> = Map.ofEntries(
 *          Tuple.of(1, 1L),
 *          Tuple.of(2, ""),
 *          Tuple.of(3, new Object())
 *      );
 * }</pre>
 * Which means that you can also access tuple data as you would a map entry:
 * <pre>{@code
 *      int i = t.getKey();
 *      String msg = t.getValue();
 * }</pre>
 * In terms of overall behaviour, it is similar to a {@code UnmodifiableEntry}.
 *
 * @author dabico
 * @param <L> The type of the first value.
 * @param <R> The type of the second value.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Tuple<L, R> implements Map.Entry<L, R> {

    L left;
    R right;

    @Override
    public L getKey() {
        return left;
    }

    @Override
    public R getValue() {
        return right;
    }

    @Override
    public R setValue(R value) {
        throw new UnsupportedOperationException("Tuple values are not modifiable!");
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tuple<?, ?> tuple = (Tuple<?, ?>) o;
        if (!Objects.equals(left, tuple.left)) return false;
        return Objects.equals(right, tuple.right);
    }

    @Override
    public int hashCode() {
        int result = left != null ? left.hashCode() : 0;
        result = 31 * result + (right != null ? right.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s)", left.toString(), right.toString());
    }

    public Tuple<R, L> invert() {
        return new Tuple<>(right, left);
    }

    public static <L, R> Tuple<L, R> of(L left, R right) {
        return new Tuple<>(left, right);
    }
}

