package ch.usi.si.seart.analyzer.extract;

import ch.usi.si.seart.treesitter.Language;

public abstract class PythonExtractor extends QueryBasedExtractor {

    @Override
    protected Language getLanguage() {
        return Language.PYTHON;
    }
}
