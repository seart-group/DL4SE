package ch.usi.si.seart.analyzer.util.stream;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.Set;
import java.util.StringJoiner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class MinimumSizeLimitCollector implements Collector<CharSequence, StringJoiner, String> {

    int limit;
    AtomicInteger count = new AtomicInteger();

    @Override
    public Supplier<StringJoiner> supplier() {
        return () -> new StringJoiner(getDelimiter());
    }

    @Override
    public BiConsumer<StringJoiner, CharSequence> accumulator() {
        return (joiner, sequence) -> {
            count.incrementAndGet();
            joiner.add(sequence);
        };
    }

    @Override
    public BinaryOperator<StringJoiner> combiner() {
        return StringJoiner::merge;
    }

    @Override
    public Function<StringJoiner, String> finisher() {
        return (joiner) -> {
            String joined = joiner.toString();
            return (count.get() >= limit) ? getPrefix() + joined + getSuffix() : joined;
        };
    }

    @Override
    public Set<Characteristics> characteristics() {
        return Collections.emptySet();
    }

    protected abstract String getDelimiter();

    protected abstract String getPrefix();

    protected abstract String getSuffix();
}
