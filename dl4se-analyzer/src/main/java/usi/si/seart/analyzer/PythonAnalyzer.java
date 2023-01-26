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
        this.testFilePredicate = new PythonTestFilePredicate();
        this.singleCaptureQueries = new PythonSingleCaptureQueries();
        this.multiCaptureQueries = new PythonMultiCaptureQueries();
        // this.boilerplateEnumerator = new PythonBoilerplateEnumerator(this::getSourceBytes);
    }
}
