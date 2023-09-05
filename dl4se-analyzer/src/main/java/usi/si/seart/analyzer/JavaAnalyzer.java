package usi.si.seart.analyzer;

import ch.usi.si.seart.treesitter.Language;

import java.nio.file.Path;

public class JavaAnalyzer extends AbstractAnalyzer {

    public JavaAnalyzer(LocalClone localClone, Path path) {
        super(localClone, path, Language.JAVA);
    }
}
