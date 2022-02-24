package usi.si.seart.collection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.util.Map;

/**
 * Simple implementation of a tuple class, used as a container for two values of arbitrary types.
 * Tuples are immutable, and can only be created through a static factory:
 * <pre>{@code
 *      Tuple<Integer, String> t = Tuple.of(1, "Hello!");
 * }</pre>
 * Values are subsequently accessed through getters:
 * <pre>{@code
 *      int i = t.getLeft();
 *      String msg = t.getRight();
 * }</pre>
 * This class can also be used in place of {@link Map.Entry Entry}:
 * <pre>{@code
 *      Map<Integer, Object> = Map.ofEntries(
 *          Tuple.of(1, 1L),
 *          Tuple.of(2, ""),
 *          Tuple.of(3, new Object())
 *      );
 * }</pre>
 * In terms of behaviour, it is similar to {@code UnmodifiableEntry}.
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
        return this.left;
    }

    @Override
    public R getValue() {
        return this.right;
    }

    @Override
    public R setValue(R value) {
        throw new UnsupportedOperationException("Tuple values are not modifiable!");
    }

    public static <L, R> Tuple<L, R> of(L left, R right) {
        return new Tuple<>(left, right);
    }
}
