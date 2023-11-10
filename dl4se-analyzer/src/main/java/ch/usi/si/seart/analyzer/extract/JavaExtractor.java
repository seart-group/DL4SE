package ch.usi.si.seart.analyzer.extract;

import ch.usi.si.seart.treesitter.Language;

public abstract class JavaExtractor extends QueryBasedExtractor {

    protected JavaExtractor() {
        super(Language.JAVA);
    }
}
