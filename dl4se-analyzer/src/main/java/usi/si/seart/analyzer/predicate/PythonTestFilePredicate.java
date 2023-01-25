package usi.si.seart.analyzer.predicate;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;

public class PythonTestFilePredicate extends TestFilePredicate {

    @Override
    public boolean test(Path path) {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**/*test*.py");
        return super.test(path) || matcher.matches(path);
    }
}
