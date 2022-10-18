package usi.si.seart.io;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Class used to traverse a directory structure, and save references to all files whose extension matches at least one
 * of the extensions specified in the constructor.
 * Creating with no extensions specified will match all files.
 * Use as follows:
 *
 * <pre>{@code
 *     // This visitor matches all Java, Python and C files
 *     ExtensionBasedFileVisitor visitor = new ExtensionBasedFileVisitor("java", "py", "c");
 *     Files.walkFileTree(Path.of("/start/path"), visitor);
 *     List<Path> javaFiles = visitor.getVisited();
 * }</pre>
 *
 * @see java.nio.file.SimpleFileVisitor SimpleFileVisitor
 * @see java.nio.file.Files#walkFileTree(Path, java.nio.file.FileVisitor) Files.walkFileTree
 * @author dabico
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ExtensionBasedFileVisitor extends SimpleFileVisitor<Path> {

    PathMatcher matcher;

    @Getter
    List<Path> visited;

    /**
     * Static factory used for creating instances of the class. We use it in order to enforce pre-conditions before any
     * actual class instances are created.
     *
     * @param extensions File extension names. Must not be {@code null} or contain any {@code null} values, empty or
     *                   blank strings.
     * @return A new instance of the class.
     */
    public static ExtensionBasedFileVisitor forExtensions(String... extensions) {
        Objects.requireNonNull(extensions);
        validateExtensions(extensions);
        return new ExtensionBasedFileVisitor(extensions);
    }

    private static void validateExtensions(String[] extensions) {
        for (String extension: extensions)
            if (extension == null || extension.isBlank())
                throw new IllegalArgumentException("Invalid extension: " + extension);
    }

    private ExtensionBasedFileVisitor(String... extensions) {
        super();
        this.matcher = compileMatcher(extensions);
        this.visited = new ArrayList<>();
    }

    private PathMatcher compileMatcher(String... extensions) {
        StringBuilder sb = new StringBuilder("glob:*");
        if (extensions.length > 0) {
            String extStr = String.join(",", extensions);
            sb.append("\\.{").append(extStr).append("}");
        }
        return FileSystems.getDefault().getPathMatcher(sb.toString());
    }

    /**
     * {@inheritDoc}
     *
     * If the file extension matches the visitor pattern, then it is recorded in the list of visited files.
     *
     * @param path  a reference to the file
     * @param attrs the file's basic attributes
     * @return the visit result
     */
    @Override
    public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) {
        if (matcher.matches(path.getFileName())) {
            visited.add(path);
        }
        return FileVisitResult.CONTINUE;
    }

    /**
     * {@inheritDoc}
     *
     * Unlike its superclass, this variant just silently ignores the exception.
     *
     * @param path a reference to the file
     * @param ex   the I/O exception that prevented the file from being visited
     */
    @Override
    public FileVisitResult visitFileFailed(Path path, IOException ex) {
        return FileVisitResult.CONTINUE;
    }

    /**
     * Invoked for a directory before entries in the directory are visited.
     * Always returns {@link FileVisitResult#CONTINUE CONTINUE} to ensure that directory contents are visited.
     *
     * @param path  a reference to the directory
     * @param attrs the directory's basic attributes
     */
    @Override
    public FileVisitResult preVisitDirectory(Path path, BasicFileAttributes attrs) {
        return FileVisitResult.CONTINUE;
    }

    /**
     * Invoked for a directory after entries in the directory, and all of their descendants, have been visited.
     * Always returns {@link FileVisitResult#CONTINUE CONTINUE}, signifying that all contents have been visited.
     *
     * @param dir a reference to the directory
     * @param ex  {@code null} if the iteration of the directory completes without
     *            an error; otherwise the I/O exception that caused the iteration
     *            of the directory to complete prematurely
     */
    @Override
    public FileVisitResult postVisitDirectory(Path dir, IOException ex) {
        return FileVisitResult.CONTINUE;
    }
}
