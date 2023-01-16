package usi.si.seart.analyzer.predicate;

import usi.si.seart.analyzer.NodeMapper;
import usi.si.seart.treesitter.Node;

import java.util.function.Predicate;

public abstract class ContentPredicate implements Predicate<Node> {

    protected final NodeMapper mapper;

    protected ContentPredicate(NodeMapper mapper) {
        this.mapper = mapper;
    }
}
