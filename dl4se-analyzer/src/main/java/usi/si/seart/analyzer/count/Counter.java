package usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;

import java.util.Collection;
import java.util.stream.Stream;

public interface Counter {
    Long count(Node node);

    default Long count(Node... nodes) {
        return Stream.of(nodes).mapToLong(this::count).sum();
    }

    default Long count(Collection<Node> nodes) {
        return nodes.stream().mapToLong(this::count).sum();
    }
}
