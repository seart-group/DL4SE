package usi.si.seart.analyzer.predicate;

import usi.si.seart.treesitter.Language;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.function.Predicate;

public abstract class TestFilePredicate implements Predicate<Path> {

    @Override
    public boolean test(Path path) {
        String glob = "glob:{**/test/**,**/tests/**}";
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher(glob);
        return matcher.matches(path);
    }

    public static TestFilePredicate forLanguage(Language language) {
        switch (language) {
            case JAVA: return new JavaTestFilePredicate();
            default: return new TestFilePredicate() {};
        }
    }

}
