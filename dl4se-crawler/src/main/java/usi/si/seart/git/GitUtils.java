package usi.si.seart.git;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@Slf4j
@UtilityClass
public class GitUtils {

    private final String gitHubLinkPattern = "https://github.com/%s.git";

    /**
     * Wrapper for JGit API, allowing for more straight-forward use.
     * Clones a repository based on its full name: {user}/{repo}.
     * Example usage:
     *
     * <pre>{@code
     *     File ghs = GitUtils.cloneRepository("seart-group/ghs");
     * }</pre>
     *
     * @param name The full GitHub repository name.
     * @return Reference to the directory containing the clone contents.
     * @throws GitAPIException
     * In case an error occurs with JGit (i.e. specified remote is invalid).
     */
    @SneakyThrows({IOException.class})
    public File cloneRepository(String name) throws GitAPIException {
        log.debug("Started Cloning: {}", name);
        File dir = Files.createTempDirectory("dl4se").toFile();
        Git.cloneRepository()
                .setURI(String.format(gitHubLinkPattern, name))
                .setDirectory(dir)
                .call();
        log.debug("Finished Cloning: {}", name);
        return dir;
    }
}
