package ch.usi.si.seart.analyzer.predicate.node;

import ch.usi.si.seart.treesitter.Node;

import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Stream;

public abstract class NodePredicate implements Predicate<Node> {

    public abstract boolean test(Node node);

    public boolean test(Node... nodes) {
        return test(Stream.of(nodes));
    }

    public boolean test(Collection<Node> nodes) {
        return test(nodes.stream());
    }

    protected boolean test(Stream<Node> nodes) {
        return nodes.anyMatch(this);
    }
}
