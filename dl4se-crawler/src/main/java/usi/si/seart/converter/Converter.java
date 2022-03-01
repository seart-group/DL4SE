package usi.si.seart.converter;

import java.util.Iterator;
import java.util.Objects;

/**
 * Based on Google's {@link com.google.common.base.Converter Converter}, albeit with some minor differences.
 * A {@code Converter} maps an instance of type {@code A} to an instance of type {@code B}.
 * {@code null} handling is done automatically. Each implementing class should follow a singleton pattern,
 * reusing a single eagerly initialized instance throughout the application code.
 *
 * @param <A> The type to convert from
 * @param <B> The type to convert to
 */
public abstract class Converter<A, B> {

    private Converter<B, A> reverse;
    
    protected abstract B forward(A a);
    protected abstract A backward(B b);

    B doForward(A a) {
        if (a != null) return forward(a);
        else return null;
    }

    A doBackward(B b) {
        if (b != null) return backward(b);
        else return null;
    }

    /**
     * Method used for performing conversions.
     *
     * @return The input value, converted from type {@code A} to {@code B}, or {@code null} if and only if the input is also {@code null}.
     */
    public final B convert(A a) {
        return doForward(a);
    }

    /**
     * Returns an {@code Iterable} that applies {@code convert} to each element of {@code fromIterable}. The
     * conversion is done lazily.
     *
     * <p>The returned iterable's iterator supports {@code remove()} if the input iterator does. After
     * a successful {@code remove()} call, {@code fromIterable} no longer contains the corresponding
     * element.
     *
     * @param aIterable The {@code Iterable} whose instances we wish to convert.
     */
    public Iterable<B> convertAll(Iterable<A> aIterable) {
        Objects.requireNonNull(aIterable);
        return () -> new Iterator<>() 
        {
            private final Iterator<? extends A> aIterator = aIterable.iterator();

            @Override
            public boolean hasNext() {
                return aIterator.hasNext();
            }

            @Override
            public B next() {
                return convert(aIterator.next());
            }

            @Override
            public void remove() {
                aIterator.remove();
            }
        };
    }

    /**
     * Method used for obtaining the reversed view of this converter.
     *
     * @return A {@code Converter} from {@code B} to {@code A}.
     */
    public Converter<B, A> reverse() {
        if (this.reverse == null) {
            reverse = new ReverseConverter<>(this);
        }

        return reverse;
    }

    private static final class ReverseConverter<A, B> extends Converter<B, A> {
        final Converter<A, B> original;

        ReverseConverter(Converter<A, B> original) {
            this.original = original;
        }

        @Override
        protected A forward(B b) {
            throw new UnsupportedOperationException();
        }

        @Override
        protected B backward(A a) {
            throw new UnsupportedOperationException();
        }

        @Override
        A doForward(B b) {
            return original.doBackward(b);
        }

        @Override
        B doBackward(A a) {
            return original.doForward(a);
        }

        @Override
        public Converter<A, B> reverse() {
            return original;
        }
    }
}
