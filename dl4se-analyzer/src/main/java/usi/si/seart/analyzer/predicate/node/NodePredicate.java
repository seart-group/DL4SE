package usi.si.seart.analyzer.predicate.node;

import usi.si.seart.treesitter.Node;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class NodePredicate implements Predicate<Node> {
    public abstract boolean test(Node node);

    public boolean test(Node... nodes) {
        return Stream.of(nodes).anyMatch(this);
    }

    public boolean test(Collection<Node> nodes) {
        return nodes.stream().anyMatch(this);
    }
}
