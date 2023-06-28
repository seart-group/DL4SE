package usi.si.seart.analyzer;

import lombok.experimental.UtilityClass;
import usi.si.seart.model.Language;

import java.nio.file.Path;
import java.util.function.BiFunction;

@UtilityClass
public class AnalyzerFactory {

    public BiFunction<LocalClone, Path, Analyzer> getAnalyzer(Language language) {
        return getAnalyzer(language.getName());
    }

    private BiFunction<LocalClone, Path, Analyzer> getAnalyzer(String name) {
        switch (name) {
            case "Java":
                return JavaAnalyzer::new;
            case "Python":
                return PythonAnalyzer::new;
            default:
                throw new UnsupportedOperationException("The language [" + name + "] is not supported!");
        }
    }
}
