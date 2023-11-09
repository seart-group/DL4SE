package ch.usi.si.seart.analyzer.extract;

import ch.usi.si.seart.treesitter.Language;

public abstract class PythonExtractor extends QueryBasedExtractor {

    protected PythonExtractor() {
        super(Language.PYTHON);
    }
}
