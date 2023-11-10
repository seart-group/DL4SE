package ch.usi.si.seart.analyzer.extract;

import ch.usi.si.seart.treesitter.Language;

import java.util.Collections;

public abstract class FunctionExtractorFactory {

    public static Extractor getInstance(Language language) {
        switch (language) {
            case JAVA: return new JavaCallableDeclarationExtractor();
            case PYTHON: return new PythonFunctionExtractor();
            default: return tree -> Collections.emptyList();
        }
    }
}
