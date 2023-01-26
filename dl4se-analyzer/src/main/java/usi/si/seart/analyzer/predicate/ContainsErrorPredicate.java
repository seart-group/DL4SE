package usi.si.seart.analyzer.predicate;

import usi.si.seart.treesitter.Node;

public class ContainsErrorPredicate extends NodePredicate {

    @Override
    public boolean test(Node node) {
        return node.hasError();
    }
}
