package usi.si.seart.analyzer.printer;

import usi.si.seart.treesitter.Node;

import java.util.Collections;
import java.util.Set;
import java.util.StringJoiner;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class SymbolicExpressionPrinter extends AbstractPrinter {

    @Override
    public String print(Node node) {
        return node.getNodeString();
    }

    @Override
    protected Collector<CharSequence, ?, String> resultCollector() {
        return new Collector<CharSequence, StringJoiner, String>() {

            int count = 0;

            @Override
            public Supplier<StringJoiner> supplier() {
                return () -> new StringJoiner(" ");
            }

            @Override
            public BiConsumer<StringJoiner, CharSequence> accumulator() {
                return (joiner, sequence) -> {
                    count++;
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
                    return  (count > 1) ? "(" + joined + ")" : joined;
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return Collections.emptySet();
            }
        };
    }
}
