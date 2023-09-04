package usi.si.seart.analyzer.printer;

import ch.usi.si.seart.treesitter.Node;

import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractPrinter implements Printer {

    @Override
    public String print(Node... nodes) {
        return print(Stream.of(nodes));
    }

    @Override
    public String print(Collection<Node> nodes) {
        return print(nodes.stream());
    }

    protected String print(Stream<Node> nodes) {
        return nodes.map(this::print).collect(resultCollector());
    }

    protected Collector<CharSequence, ?, String> resultCollector() {
        return Collectors.joining();
    }
}
