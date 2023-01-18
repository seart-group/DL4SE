package usi.si.seart.analyzer.util.stream;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.util.Collections;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

@AllArgsConstructor
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DelimiterSuffixedStringCollector implements Collector<String, StringJoiner, String> {

    final String delimiter;
    String empty = "";

    @Override
    public Supplier<StringJoiner> supplier() {
        return () -> new StringJoiner(delimiter, "", delimiter).setEmptyValue(empty);
    }

    @Override
    public BiConsumer<StringJoiner, String> accumulator() {
        return StringJoiner::add;
    }

    @Override
    public BinaryOperator<StringJoiner> combiner() {
        return StringJoiner::merge;
    }

    @Override
    public Function<StringJoiner, String> finisher() {
        return StringJoiner::toString;
    }

    @Override
    public Set<Characteristics> characteristics() {
        // TODO: 18.01.23 Define optimization characteristics...
        return Collections.emptySet();
    }
}
