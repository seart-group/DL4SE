package usi.si.seart.analyzer.printer;

import usi.si.seart.analyzer.NodeMapper;

public abstract class AbstractPrinter implements Printer {

    protected final NodeMapper mapper;

    protected AbstractPrinter(NodeMapper mapper) {
        this.mapper = mapper;
    }
}
