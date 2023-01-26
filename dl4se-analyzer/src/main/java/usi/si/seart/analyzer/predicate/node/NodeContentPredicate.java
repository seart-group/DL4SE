package usi.si.seart.analyzer.predicate.node;

import usi.si.seart.analyzer.NodeMapper;

public abstract class NodeContentPredicate extends NodePredicate {

    protected final NodeMapper mapper;

    protected NodeContentPredicate(NodeMapper mapper) {
        this.mapper = mapper;
    }
}
