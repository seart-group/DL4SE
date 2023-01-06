package usi.si.seart.analyzer.count;

import usi.si.seart.treesitter.Node;

public interface Counter {
    Long count(Node node);
}
