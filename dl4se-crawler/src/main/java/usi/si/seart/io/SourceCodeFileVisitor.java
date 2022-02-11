package usi.si.seart.io;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

/**
 * Class used to traverse a directory structure, and save references to all files whose extension matches at least one
 * of the extensions specified in the constructor.
 * Creating with no extensions specified will match all files.
 * Use as follows:
 * <pre>{@code
 *     // This visitor matches all Java, Python and C files
 *     SourceCodeFileVisitor visitor = new SourceCodeFileVisitor("java", "py", "c");
 *     Files.walkFileTree(Path.of("/start/path"), visitor);
 *     List<File> javaFiles = visitor.getVisited();
 * }</pre>
 *
 * @see java.nio.file.SimpleFileVisitor SimpleFileVisitor
 * @see java.nio.file.Files Files
 * @author dabico
 */
@Slf4j
@Getter
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SourceCodeFileVisitor extends SimpleFileVisitor<Path> {

    PathMatcher includeMatcher;
    List<File> visited;

    public SourceCodeFileVisitor(String... extensions) {
        super();
        String glob = "glob:*";
        if (extensions.length > 0) {
            glob += "\\.";
            glob += "{" + String.join(",", extensions) + "}";
        }
        this.includeMatcher = FileSystems.getDefault().getPathMatcher(glob);
        this.visited = new ArrayList<>();
    }

    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
        if (includeMatcher.matches(path.getFileName())) {
            log.debug("Marking path: {}", path);
            visited.add(new File(path.toUri()));
        }
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path path, IOException ex) {
        log.error("Visit failed for path: " + path, ex);
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }

    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException ex) {
        return FileVisitResult.CONTINUE;
    }
}
