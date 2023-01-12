package usi.si.seart.analyzer.predicate;

import usi.si.seart.analyzer.NodeMapper;

public abstract class ContentPredicate implements Predicate {

    protected final NodeMapper mapper;

    protected ContentPredicate(NodeMapper mapper) {
        this.mapper = mapper;
    }
}
