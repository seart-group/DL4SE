package ch.usi.si.seart.analyzer.extract;

import ch.usi.si.seart.treesitter.Language;
import lombok.experimental.UtilityClass;

import java.util.Collections;

@UtilityClass
public class FunctionExtractorFactory {

    public Extractor getInstance(Language language) {
        switch (language) {
            case JAVA: return new JavaCallableDeclarationExtractor();
            case PYTHON: return new PythonFunctionExtractor();
            default: return tree -> Collections.emptyList();
        }
    }
}
