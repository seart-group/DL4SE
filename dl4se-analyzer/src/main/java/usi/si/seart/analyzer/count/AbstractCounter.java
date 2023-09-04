package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;

import java.util.Collection;
import java.util.stream.Stream;

public abstract class AbstractCounter implements Counter {

    public Long count(Node... nodes) {
        return count(Stream.of(nodes));
    }

    public Long count(Collection<Node> nodes) {
        return count(nodes.stream());
    }

    protected Long count(Stream<Node> nodes) {
        return nodes.mapToLong(this::count).sum();
    }
}
