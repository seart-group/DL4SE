package usi.si.seart.analyzer.hash;

import usi.si.seart.treesitter.Tree;

public interface Hasher {
    String hash(Tree tree);
}
