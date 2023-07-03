package usi.si.seart.utils;

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
     * Determine if a file is indeed a <em>test</em> file. We define test files as files whose:
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
     * For instance, using a more general pattern like {@code **test**} would match non-conforming cases such as
     * {@code /src/latest/App.java}.
     * @param path {@code Path} that we are testing against.
     * @return Whether the path in question matches the definition of a test file.
     */
    //TODO 08.03.22: Switch pattern matching based on extension
    public boolean isTestFile(Path path) {
        return testPathMatcher.matches(path);
    }

    /**
     * Extract the file extension for a given file path.
     *
     * @param path {@code Path} that we are testing against.
     * @return The file extension {@code String}. If the file has no extension or is a directory, then an empty
     * string is returned.
     */
    public String getExtension(Path path) {
        Objects.requireNonNull(path);
        String fileName = path.getFileName().toString();
        if (fileName.contains(".")) {
            int extStart = fileName.lastIndexOf(".") + 1;
            if (extStart < fileName.length() && extStart > 0) {
                return fileName.substring(extStart);
            }
        }
        return "";
    }
}
