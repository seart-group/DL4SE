package usi.si.seart.analyzer.enumerator;

import usi.si.seart.analyzer.NodeMapper;
import usi.si.seart.model.code.Boilerplate;

public abstract class BoilerplateEnumerator implements Enumerator<Boilerplate> {

    protected final NodeMapper mapper;

    protected BoilerplateEnumerator(NodeMapper mapper) {
        this.mapper = mapper;
    }
}
