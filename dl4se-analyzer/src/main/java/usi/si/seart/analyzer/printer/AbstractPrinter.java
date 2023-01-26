package usi.si.seart.analyzer.printer;

import usi.si.seart.treesitter.Node;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractPrinter implements Printer {

    @Override
    public String print(Node... nodes) {
        return Stream.of(nodes)
                .map(this::print)
                .collect(resultCollector());
    }

    @Override
    public String print(Collection<Node> nodes) {
        return nodes.stream()
                .map(this::print)
                .collect(resultCollector());
    }

    protected Collector<CharSequence, ?, String> resultCollector() {
        return Collectors.joining();
    }
}
