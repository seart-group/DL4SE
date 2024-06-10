package ch.usi.si.seart.analyzer.predicate.path;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Set;
import java.util.stream.Collectors;

public class PythonTestFilePredicate extends TestFilePredicate {

    private static final Set<String> FILE_PATTERNS = Set.of(
            "**/test.py",
            "**/*_test.py",
            "**/test_*.py"
    );

    @Override
    public boolean test(Path path) {
        String glob = FILE_PATTERNS.stream().collect(Collectors.joining(",", "glob:{", "}"));
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(glob);
        return super.test(path) || matcher.matches(path);
    }
}
