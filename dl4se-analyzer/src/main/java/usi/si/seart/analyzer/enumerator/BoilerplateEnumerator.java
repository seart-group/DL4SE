package usi.si.seart.analyzer.enumerator;

import ch.usi.si.seart.treesitter.Language;
import usi.si.seart.model.code.Boilerplate;

public abstract class BoilerplateEnumerator implements Enumerator<Boilerplate> {

    public static Enumerator<Boilerplate> getInstance(Language language) {
        switch (language) {
            case JAVA:
                return new JavaBoilerplateEnumerator();
            case PYTHON:
                return new PythonBoilerplateEnumerator();
            default:
                return node -> null;
        }
    }
}
