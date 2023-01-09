package usi.si.seart.analyzer.predicate;

import usi.si.seart.treesitter.Node;

public interface Predicate {
    Boolean test(Node node);
}
