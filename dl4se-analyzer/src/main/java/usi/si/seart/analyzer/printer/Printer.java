package usi.si.seart.analyzer.printer;

import usi.si.seart.treesitter.Node;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface Printer {
    String print(Node node);

    default String print(Node... nodes) {
        return Stream.of(nodes)
                .map(this::print)
                .collect(resultCollector());
    }

    default String print(Collection<Node> nodes) {
        return nodes.stream()
                .map(this::print)
                .collect(resultCollector());
    }

    default Collector<CharSequence, ?, String> resultCollector() {
        return Collectors.joining();
    }
}
