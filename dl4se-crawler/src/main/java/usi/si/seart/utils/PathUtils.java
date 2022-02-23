package usi.si.seart.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@UtilityClass
public class PathUtils {

    private final PathMatcher testPathMatcher;
    static {
        String globPattern = Stream.of(
                "**/test/**",
                "**/tests/**",
                "**/Test*.java",
                "**/*Test.java",
                "**/*Tests.java",
                "**/*TestCase.java",
                "**/IT*.java",
                "**/*IT.java",
                "**/*ITCase.java"
        ).collect(Collectors.joining(",", "glob:{", "}"));
        testPathMatcher = FileSystems.getDefault().getPathMatcher(globPattern);
    }


    /**
     * Path utility used to determine if a file is indeed a Java test file.
     * We define test files as files whose:
     *
     * <ul>
     *     <li>
     *         Path contains the following directories:
     *         <ul>
     *             <li>{@code test}</li>
     *             <li>{@code tests}</li>
     *         </ul>
     *     </li>
     *     <li>
     *         Name matches the following patterns:
     *         <ul>
     *             <li>{@code Test*.java}</li>
     *             <li>{@code *Test.java}</li>
     *             <li>{@code *Tests.java}</li>
     *             <li>{@code *TestCase.java}</li>
     *             <li>{@code IT*.java}</li>
     *             <li>{@code *IT.java}</li>
     *             <li>{@code *ITCase.java}</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @implNote We keep the matching restrictive to minimise the amount of false positives.
     * For instance, using a more general pattern {@code **test**} would match non-conforming cases like
     * {@code /src/latest/App.java}.
     * @param file Path to file that we are testing against.
     * @return Whether the file in question is a test file.
     */
    // TODO: 16.02.22
    //  Other determiners for test functions?
    //  Generalize to other languages?
    public boolean isTestFile(Path file) {
        return testPathMatcher.matches(file);
    }

    /**
     * Path utility used to extract the file extension.
     *
     * @param file Path to file that we are testing against.
     * @return The file extension string. If the file has no extension, then an empty string is returned.
     */
    public String getExtension(Path file) {
        Objects.requireNonNull(file);
        String path = file.getFileName().toString();
        if (path.contains(".")) {
            int extStart = path.lastIndexOf(".") + 1;
            if (extStart < path.length() && extStart > 0) {
                return path.substring(extStart);
            }
        }
        return "";
    }

    /**
     * Path utility used to forcefully delete a directory and all its contents.
     *
     * @implNote We suppress throws of {@link java.io.IOException IOException} and {@link InterruptedException}.
     * @author dabico
     * @see ProcessBuilder
     * @see <a href="https://www.baeldung.com/run-shell-command-in-java#ProcessBuilder">Run Shell Commands in Java</a>
     */
    @SneakyThrows
    public void forceDelete(Path file) {
        Objects.requireNonNull(file);
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("rm", "-rf", file.getFileName().toString());
        builder.directory(file.getParent().toFile());
        Process process = builder.start();
        process.waitFor();
    }
}
