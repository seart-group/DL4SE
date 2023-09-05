package usi.si.seart.analyzer.enumerator;

import ch.usi.si.seart.treesitter.Language;
import usi.si.seart.analyzer.NodeMapper;
import usi.si.seart.model.code.Boilerplate;

public abstract class BoilerplateEnumerator implements Enumerator<Boilerplate> {

    protected final NodeMapper mapper;

    protected BoilerplateEnumerator(NodeMapper mapper) {
        this.mapper = mapper;
    }

    public static Enumerator<Boilerplate> getInstance(Language language, NodeMapper mapper) {
        switch (language) {
            case JAVA:
                return new JavaBoilerplateEnumerator(mapper);
            case PYTHON:
                return new PythonBoilerplateEnumerator(mapper);
            default:
                return node -> null;
        }
    }
}
