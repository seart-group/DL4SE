package usi.si.seart.analyzer;

import ch.usi.si.seart.treesitter.Language;

import java.nio.file.Path;

public class PythonAnalyzer extends AbstractAnalyzer {

    public PythonAnalyzer(LocalClone localClone, Path path) {
        super(localClone, path, Language.PYTHON);
    }
}
