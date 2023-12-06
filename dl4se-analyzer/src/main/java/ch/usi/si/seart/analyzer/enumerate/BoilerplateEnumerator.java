package ch.usi.si.seart.analyzer.enumerate;

import ch.usi.si.seart.model.code.Boilerplate;
import ch.usi.si.seart.treesitter.Language;

public abstract class BoilerplateEnumerator implements Enumerator<Boilerplate> {

    public static Enumerator<Boilerplate> getInstance(Language language) {
        switch (language) {
            case JAVA: return new JavaBoilerplateEnumerator();
            case PYTHON: return new PythonBoilerplateEnumerator();
            default: return node -> null;
        }
    }
}
