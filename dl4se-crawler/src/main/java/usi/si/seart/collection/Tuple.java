package usi.si.seart.collection;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;


/**
 * Simple implementation of a Tuple class, used as a container for two values of arbitrary types.
 * Tuples are immutable, and can only be created through a static factory:
 * <pre>{@code
 *      Tuple<Integer, String> t = Tuple.of(1, "Hello!");
 * }</pre>
 * Values are subsequently accessed through getters:
 * <pre>{@code
 *      int i = t.getLeft();
 *      String msg = t.getRight();
 * }</pre>
 *
 * @author dabico
 * @param <L> The type of the first value.
 * @param <R> The type of the second value.
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Tuple<L, R> {

    L left;
    R right;

    public static <L, R> Tuple<L, R> of(L left, R right) {
        return new Tuple<>(left, right);
    }
}
