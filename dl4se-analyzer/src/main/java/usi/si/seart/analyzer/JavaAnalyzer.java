package usi.si.seart.analyzer;

import usi.si.seart.analyzer.count.JavaTokenCounter;
import usi.si.seart.analyzer.enumerator.JavaBoilerplateEnumerator;
import usi.si.seart.analyzer.predicate.path.JavaTestFilePredicate;
import usi.si.seart.analyzer.query.multi.JavaMultiCaptureQueries;
import usi.si.seart.analyzer.query.single.JavaSingleCaptureQueries;
import usi.si.seart.treesitter.Language;

import java.nio.file.Path;

public class JavaAnalyzer extends AbstractAnalyzer {

    public JavaAnalyzer(LocalClone localClone, Path path) {
        super(localClone, path, Language.JAVA);
        this.totalTokenCounter = new JavaTokenCounter(this::getSourceBytes);
        this.testFilePredicate = new JavaTestFilePredicate();
        this.singleCaptureQueries = new JavaSingleCaptureQueries();
        this.multiCaptureQueries = new JavaMultiCaptureQueries();
        this.boilerplateEnumerator = new JavaBoilerplateEnumerator(this::getSourceBytes);
    }
}
