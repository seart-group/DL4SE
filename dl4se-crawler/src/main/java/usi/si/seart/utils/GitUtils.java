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
     * Clones a repository based on its full name: {user}/{repo}, into a directory of our choosing.
     * To allow future operations on the clone, the corresponding Git object is returned.
     * Example usage:
     *
     * <pre>{@code
     *     Git ghs = GitUtils.cloneRepository("seart-group/ghs", Path.of("/path/to/dir"));
     * }</pre>
     *
     * @param name The full GitHub repository name.
     * @param dir The {@link Path} to the directory we wish to clone in.
     * @return The {@link Git} object, which can be used for other git operations on that directory in the future.
     * @throws GitAPIException In case an error occurs with JGit (i.e. specified remote is invalid).
     */
    public Git cloneRepository(String name, Path dir) throws GitAPIException {
        log.debug("Started Cloning: {}", name);
        Git git = Git.cloneRepository()
                .setURI(String.format(gitHubLinkPattern, name))
                .setDirectory(dir.toFile())
                .call();
        log.debug("Finished Cloning: {}", name);
        return git;
    }
}
