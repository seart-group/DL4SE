package usi.si.seart.analyzer.count;

import usi.si.seart.analyzer.NodeMapper;

public abstract class ContentTraverseCounter extends TraverseCounter {

    protected final NodeMapper mapper;

    protected ContentTraverseCounter(NodeMapper mapper) {
        this.mapper = mapper;
    }
}
