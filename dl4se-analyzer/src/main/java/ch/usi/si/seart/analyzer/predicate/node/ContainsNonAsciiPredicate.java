package ch.usi.si.seart.analyzer.predicate.node;

import ch.usi.si.seart.treesitter.Node;

public class ContainsNonAsciiPredicate extends NodePredicate {

    @Override
    public boolean test(Node node) {
        return node.getContent().chars().anyMatch(c -> c > 127);
    }
}
