package ch.usi.si.seart.crawler.io;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.function.Predicate;

@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GlobPathMatcherPredicate implements Predicate<Path> {

    PathMatcher matcher;

    public GlobPathMatcherPredicate(String pattern) {
        this(FileSystems.getDefault().getPathMatcher("glob:" + pattern));
    }

    @Override
    public boolean test(Path path) {
        return matcher.matches(path);
    }
}
