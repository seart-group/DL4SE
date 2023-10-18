package ch.usi.si.seart.crawler.git;

import ch.usi.si.seart.model.Language;
import com.google.common.collect.ObjectArrays;
import com.google.common.io.CharStreams;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * Class used for interacting with the Git version control system.
 *
 * @author dabico
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Git implements AutoCloseable {

    private static final String repoLinkPattern = "https://github.com/%s.git";

    Path localDir;
    String name;
    String url;

    /**
     * Creates a new {@code Git} object, performing a regular clone operation in the process.
     * Clones a repository based on its full name: {@code {user}/{repo}}, into a directory of our choosing.
     * Primary use case is when we are interested in the commit history of a repository.
     * For the operation to succeed, the chosen directory <b>must be empty</b>.
     *
     * @param name The fully qualified name of the repository
     * @param localDir The directory in which the repository will be cloned
     * @throws GitException In case the remote does not exist, or the supplied directory is not empty.
     */
    public Git(String name, Path localDir) throws GitException {
        this(name, localDir, false);
    }

    /**
     * Creates a new {@code Git} object, performing a regular or shallow clone operation in the process.
     * Clones a repository based on its full name: {@code {user}/{repo}}, into a directory of our choosing.
     * A shallow clone includes all files from the latest revision of the default branch, but no project history
     * information (save for the latest commit). As such, the primary use case is when we are interested only in the
     * repository file contents of the latest snapshot. For the operation to succeed, the chosen directory
     * <b>must be empty</b>.
     *
     * @param name The fully qualified name of the repository
     * @param localDir The directory in which the repository will be cloned
     * @param shallow Whether the clone should be shallow
     * @throws GitException In case the remote does not exist, or the supplied directory is not empty.
     */
    public Git(String name, Path localDir, boolean shallow) throws GitException {
        this.name = name;
        this.localDir = localDir;
        this.url = String.format(repoLinkPattern, name);
        if (shallow) shallow();
        else regular();
    }

    private void regular() throws GitException {
        Process process = executeGitCommand("clone", url, ".");
        checkFailure(process);
    }

    private void shallow() throws GitException {
        Process process = executeGitCommand("clone", url, "--depth=1", ".");
        checkFailure(process);
    }

    /**
     * Creates a new {@code Git} object, performing a shallow clone operation.
     * Clones a repository based on its full name: {@code {user}/{repo}}, into a directory of our choosing.
     * This form of shallow clone includes all files from the latest revision of the default branch,
     * as well as the project's version history starting from the specified date up to the latest commit.
     * As such, the primary use case is when we are interested in analyzing changes made during a period of time.
     * For the operation to succeed, the chosen directory <b>must be empty</b>.
     * Furthermore, the depth limit date <b>must not be greater than the last commit date</b>.
     *
     * @param name The fully qualified name of the repository
     * @param localDir The directory in which the repository will be cloned
     * @param since The date and time up to which the history will be extracted
     * @throws GitException In case the remote does not exist, the supplied directory is not empty, or the date
     * depth limit is after the date of the last commit.
     */
    public Git(String name, Path localDir, LocalDateTime since) throws GitException {
        this.name = name;
        this.localDir = localDir;
        this.url = String.format(repoLinkPattern, name);
        shallowSince(since);
    }

    private void shallowSince(LocalDateTime since) throws GitException {
        Process process = executeGitCommand("clone", url , "--shallow-since="+since, ".");
        checkFailure(process);
    }

    /**
     * Used to retrieve information regarding the latest project {@code Commit} made on the default branch.
     *
     * @return A {@code Commit} object, containing the latest commit information.
     * @throws GitException If the repository is empty (has no commits).
     * @see <a href="https://www.git-scm.com/docs/git-log">Git Log Documentation</a>
     */
    public Git.Commit getLastCommitInfo() throws GitException {
        return new Commit();
    }

    /**
     * Class used to represent a Git commit.
     */
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public class Commit {
        String sha;
        LocalDateTime timestamp;

        private Commit() throws GitException {
            Process process = executeGitCommand("log", "-1", "--format=%H%n%at");
            checkFailure(process);

            String output = stringifyInputStream(process.getInputStream());
            List<String> outputLines = output.lines().collect(Collectors.toList());
            this.sha = outputLines.get(0);
            Instant lastUpdateInstant = Instant.ofEpochSecond(Integer.parseInt(outputLines.get(1)));
            this.timestamp = LocalDateTime.ofInstant(lastUpdateInstant, ZoneId.of("UTC"));
        }
    }

    /**
     * Used to retrieve a summary of changes made between the specified commit and the repository {@code HEAD}.
     * The changelist excludes changes from the starting commit.
     *
     * @param startSHA The start commit SHA.
     * @return A {@code Diff} object, summarizing the different types of changes made to the files.
     * @throws GitException If the commit SHA is malformed or invalid.
     * @see <a href="https://git-scm.com/docs/git-diff">Git Diff Documentation</a>
     */
    public Git.Diff getDiff(String startSHA) throws GitException {
        return new Diff(startSHA, "HEAD");
    }

    /**
     * Used to retrieve a summary of changes made between the specified commit and the repository {@code HEAD}.
     * The changelist excludes changes from the starting commit, and is limited to files whose language is contained in
     * the supplied {@code Language} set.
     *
     * @param startSHA The start commit SHA.
     * @param languages The set of languages to filter by.
     * @return A {@code Diff} object, summarizing the different types of changes made to the files.
     * @throws GitException If the commit SHA is malformed or invalid.
     * @see <a href="https://git-scm.com/docs/git-diff">Git Diff Documentation</a>
     */
    public Git.Diff getDiff(String startSHA, Set<Language> languages) throws GitException {
        String[] extensions = languages.stream()
                .map(Language::getExtensions)
                .flatMap(Collection::stream)
                .map(ext -> "***." + ext)
                .toArray(String[]::new);
        return new Diff(startSHA, "HEAD", extensions);
    }

    /**
     * Used to retrieve a summary of changes made between two specified commits.
     * The changelist excludes changes from the starting commit, but includes those made in the end commit.
     *
     * @param startSHA The start commit SHA.
     * @param endSHA The end commit SHA.
     * @return A {@code Diff} object, summarizing the different types of changes made to the files.
     * @throws GitException If either of the commit SHAs is malformed or invalid.
     * @see <a href="https://git-scm.com/docs/git-diff">Git Diff Documentation</a>
     */
    public Git.Diff getDiff(String startSHA, String endSHA) throws GitException {
        return new Diff(startSHA, endSHA);
    }

    /**
     * Used to retrieve a summary of changes made between two specified commits.
     * The changelist excludes changes from the starting commit, but includes those made in the end commit.
     * It is also limited to files whose language is contained in the supplied {@code Language} set.
     *
     * @param startSHA The start commit SHA.
     * @param endSHA The end commit SHA.
     * @param languages The set of languages to filter by.
     * @return A {@code Diff} object, summarizing the different types of changes made to the files.
     * @throws GitException If either of the commit SHAs is malformed or invalid.
     * @see <a href="https://git-scm.com/docs/git-diff">Git Diff Documentation</a>
     */
    public Git.Diff getDiff(String startSHA, String endSHA, Set<Language> languages) throws GitException {
        String[] extensions = languages.stream()
                .map(Language::getExtensions)
                .flatMap(Collection::stream)
                .map(ext -> "***." + ext)
                .toArray(String[]::new);
        return new Diff(startSHA, endSHA, extensions);
    }

    /**
     * Class used to represent a {@code diff}: the changes made between commits.
     * It serves as a container for 6 types of file changes:
     * <ul>
     *     <li>{@code added} files ({@code A})</li>
     *     <li>{@code deleted} files ({@code D})</li>
     *     <li>{@code modified} files ({@code M})</li>
     *     <li>{@code renamed} files ({@code R100})</li>
     *     <li>{@code edited} files ({@code R0XX})</li>
     *     <li>{@code copied} files ({@code CXXX})</li>
     * </ul>
     */
    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    public class Diff {
        List<Path> added = new ArrayList<>();
        List<Path> deleted = new ArrayList<>();
        List<Path> modified = new ArrayList<>();
        Map<Path, Path> renamed = new HashMap<>();
        Map<Path, Path> edited = new HashMap<>();
        Map<Path, Path> copied = new HashMap<>();

        Consumer<String> addedConsumer = singlePathConsumer(added);
        Consumer<String> deletedConsumer = singlePathConsumer(deleted);
        Consumer<String> modifiedConsumer = singlePathConsumer(modified);
        Consumer<String> renamedConsumer = doublePathConsumer(renamed);
        Consumer<String> editedConsumer = doublePathConsumer(edited);
        Consumer<String> copiedConsumer = doublePathConsumer(copied);

        private Consumer<String> singlePathConsumer(List<Path> structure) {
            return line -> {
                String[] tokens = line.split("\t");
                Path path = Path.of(tokens[1]);
                structure.add(path);
            };
        }

        private Consumer<String> doublePathConsumer(Map<Path, Path> structure) {
            return line -> {
                String[] tokens = line.split("\t");
                Path before = Path.of(tokens[1]);
                Path after = Path.of(tokens[2]);
                structure.put(before, after);
            };
        }

        private Diff(String startSHA, String endSHA) throws GitException {
            Process process = executeGitCommand("diff", "--name-status", "--diff-filter=ADMRC", startSHA, endSHA);
            processOutput(process);
        }

        private Diff(String startSHA, String endSHA, String... extensions) throws GitException {
            String[] base = new String[] { "diff", "--name-status", "--diff-filter=ADMRC", startSHA, endSHA, "--" };
            String[] command = ObjectArrays.concat(base, extensions, String.class);
            Process process = executeGitCommand(command);
            processOutput(process);
        }

        private void processOutput(Process process) throws GitException {
            checkFailure(process);

            String output = stringifyInputStream(process.getInputStream());
            output.lines().forEach(line -> {
                Consumer<String> consumer;
                char status = line.charAt(0);
                switch (status) {
                    case 'A': consumer = addedConsumer; break;
                    case 'D': consumer = deletedConsumer; break;
                    case 'M': consumer = modifiedConsumer; break;
                    case 'C': consumer = copiedConsumer; break;
                    default:
                        String type = line.split("\t")[0];
                        if (type.equals("R100")) consumer = renamedConsumer;
                        else if (type.matches("R0\\d\\d")) consumer = editedConsumer;
                        else throw new IllegalArgumentException("Unknown change type: ["+type+"], in diff line: " + line);
                }
                consumer.accept(line);
            });
        }

        /**
         * @return A simplified output of the calculated diff.
         * The percentages for copied and edited files are not preserved.
         */
        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            added.forEach(path -> builder.append("A\t").append(path).append('\n'));
            deleted.forEach(path -> builder.append("D\t").append(path).append('\n'));
            modified.forEach(path -> builder.append("M\t").append(path).append('\n'));
            renamed.forEach((p1, p2) -> builder.append("R\t").append(p1).append('\t').append(p2).append('\n'));
            copied.forEach((p1, p2) -> builder.append("C\t").append(p1).append('\t').append(p2).append('\n'));
            edited.forEach((p1, p2) -> builder.append("E\t").append(p1).append('\t').append(p2).append('\n'));
            return builder.toString();
        }
    }

    private void checkFailure(Process process) throws GitException {
        if (process.exitValue() != 0){
            String errorMessage = stringifyInputStream(process.getErrorStream());
            throw new GitException("Operation failed for ["+name+"]:\n" + errorMessage);
        }
    }

    @SneakyThrows({IOException.class, InterruptedException.class})
    private Process executeGitCommand(String... args) {
        List<String> command = new ArrayList<>();
        command.add("git");
        command.addAll(Arrays.asList(args));

        ProcessBuilder builder = new ProcessBuilder(command);
        builder.directory(localDir.toFile());
        Process process = builder.start();
        process.waitFor();
        return process;
    }

    @SneakyThrows(IOException.class)
    public String stringifyInputStream(InputStream source) {
        try (Reader reader = new InputStreamReader(source)) {
            return CharStreams.toString(reader);
        }
    }

    @Override
    @SneakyThrows(InterruptedException.class)
    public void close() throws IOException {
        ProcessBuilder builder = new ProcessBuilder();
        builder.command("rm", "-rf", localDir.getFileName().toString());
        builder.directory(localDir.getParent().toFile());
        Process process = builder.start();
        process.waitFor();
    }
}
