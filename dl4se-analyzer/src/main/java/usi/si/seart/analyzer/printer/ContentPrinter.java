package usi.si.seart.analyzer.printer;

import usi.si.seart.analyzer.NodeMapper;

public abstract class ContentPrinter extends AbstractPrinter {

    protected final NodeMapper mapper;

    protected ContentPrinter(NodeMapper mapper) {
        this.mapper = mapper;
    }
}
