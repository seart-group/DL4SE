package usi.si.seart.analyzer;

import usi.si.seart.analyzer.enumerator.JavaBoilerplateEnumerator;
import usi.si.seart.analyzer.predicate.JavaTestFilePredicate;
import usi.si.seart.analyzer.query.JavaSingleCaptureQueries;
import usi.si.seart.treesitter.Language;

import java.nio.file.Path;

public class JavaAnalyzer extends PreviousCommentsAnalyzer {

    public JavaAnalyzer(LocalClone localClone, Path path) {
        super(localClone, path, Language.JAVA);
        this.testFilePredicate = new JavaTestFilePredicate();
        this.queries = new JavaSingleCaptureQueries();
        this.boilerplateEnumerator = new JavaBoilerplateEnumerator(this::getSourceBytes);
    }
}
