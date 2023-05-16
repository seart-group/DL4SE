package usi.si.seart.analyzer;

import usi.si.seart.analyzer.enumerator.PythonBoilerplateEnumerator;
import usi.si.seart.analyzer.predicate.path.PythonTestFilePredicate;
import usi.si.seart.analyzer.query.multi.PythonMultiCaptureQueries;
import usi.si.seart.analyzer.query.single.PythonSingleCaptureQueries;
import usi.si.seart.treesitter.Language;

import java.nio.file.Path;

public class PythonAnalyzer extends AbstractAnalyzer {

    public PythonAnalyzer(LocalClone localClone, Path path) {
        super(localClone, path, Language.PYTHON);
        // TODO: 16.05.23 Add dedicated counters for all/code-only tokens
        // this.codeTokenCounter = new PythonCodeTokenCounter();
        // this.totalTokenCounter = new PythonTokenCounter(this::getSourceBytes);
        this.testFilePredicate = new PythonTestFilePredicate();
        this.singleCaptureQueries = new PythonSingleCaptureQueries();
        this.multiCaptureQueries = new PythonMultiCaptureQueries();
        this.boilerplateEnumerator = new PythonBoilerplateEnumerator(this::getSourceBytes);
    }
}
