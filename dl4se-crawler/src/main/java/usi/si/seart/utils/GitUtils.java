package usi.si.seart.utils;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;

@Slf4j
@UtilityClass
public class GitUtils {

    private final String gitHubLinkPattern = "https://github.com/%s.git";

    /**
     * Wrapper for JGit API, allowing for more straight-forward use.
     * Clones a repository based on its full name: {user}/{repo}.
     * The directory containing the cloned contents is kept in the system-specific temporary directory.
     * To ease access (and subsequent clean-up), a reference to this directory is returned.
     * Example usage:
     *
     * <pre>{@code
     *     Path ghs = GitUtils.cloneRepository("seart-group/ghs");
     * }</pre>
     *
     * @implNote We suppress any {@link IOException} that might be thrown by
     * {@link Files#createTempDirectory(String, FileAttribute[]) Files.createTempDirectory}.
     * @param name The full GitHub repository name.
     * @return Reference to the directory containing the clone contents.
     * @throws GitAPIException In case an error occurs with JGit (i.e. specified remote is invalid).
     */
    @SneakyThrows({IOException.class})
    public Path cloneRepository(String name) throws GitAPIException {
        return cloneRepository(name, Files.createTempDirectory("dl4se"));
    }

    /**
     * Wrapper for JGit API, allowing for more straight-forward use.
     * Clones a repository based on its full name: {user}/{repo}, into a directory of our choosing.
     * To ease access (and subsequent clean-up), a reference to this directory is returned.
     * Example usage:
     *
     * <pre>{@code
     *     Path ghs = GitUtils.cloneRepository("seart-group/ghs", Path.of("/path/to/dir"));
     * }</pre>
     *
     * @param name The full GitHub repository name.
     * @param dir The {@link Path} to the directory we wish to clone in.
     * @return Reference to the directory containing the clone contents.
     * @throws GitAPIException In case an error occurs with JGit (i.e. specified remote is invalid).
     */
    public Path cloneRepository(String name, Path dir) throws GitAPIException {
        log.debug("Started Cloning: {}", name);
        Git.cloneRepository()
                .setURI(String.format(gitHubLinkPattern, name))
                .setDirectory(dir.toFile())
                .call();
        log.debug("Finished Cloning: {}", name);
        return dir;
    }
}
