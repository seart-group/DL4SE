package ch.usi.si.seart.analyzer.predicate.node;

import ch.usi.si.seart.treesitter.Node;

public class ContainsErrorPredicate extends NodePredicate {

    @Override
    public boolean test(Node node) {
        return node.hasError();
    }
}
