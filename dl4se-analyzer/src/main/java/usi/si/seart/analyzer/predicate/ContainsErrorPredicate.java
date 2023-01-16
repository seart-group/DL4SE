package usi.si.seart.analyzer.predicate;

import usi.si.seart.treesitter.Node;

import java.util.function.Predicate;

public class ContainsErrorPredicate implements Predicate<Node> {

    @Override
    public boolean test(Node node) {
        return node.hasError();
    }
}
