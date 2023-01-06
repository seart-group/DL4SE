package usi.si.seart.analyzer.hash;

import usi.si.seart.treesitter.Node;

public interface Hasher {
    String hash(Node node);
}
