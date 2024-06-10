package ch.usi.si.seart.analyzer.hash;

import ch.usi.si.seart.treesitter.Node;

import java.util.Collection;

public interface Hasher {

    String hash(Node node);
    String hash(Node... nodes);
    String hash(Collection<Node> nodes);
}
