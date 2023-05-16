package usi.si.seart.analyzer.traverser;

import ch.usi.si.seart.treesitter.Node;

import java.util.Collection;

public interface Traverser<C extends Collection<Node>> {
    C getNodes(Node node);
}
