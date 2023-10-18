package ch.usi.si.seart.analyzer.count;

import ch.usi.si.seart.treesitter.Node;

import java.util.Collection;

public interface Counter {

    Long count(Node node);
    Long count(Node... nodes);
    Long count(Collection<Node> nodes);
}
