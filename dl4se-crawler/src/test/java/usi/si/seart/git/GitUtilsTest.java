package usi.si.seart.git;

import lombok.SneakyThrows;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.nio.file.Path;

class GitUtilsTest {

    @TempDir
    private Path tmp;

    @Test
    @SneakyThrows
    void cloneRepositoryTest() {
        File repo = GitUtils.cloneRepository("dabico/dl4se-crawler-test", tmp).toFile();
        Assertions.assertTrue(repo.exists());
        Assertions.assertTrue(repo.isDirectory());
        File[] repoFiles = repo.listFiles();
        Assertions.assertNotNull(repoFiles);
        Assertions.assertEquals(4, repoFiles.length); // README, LICENSE, .git, .gitignore
    }

    @Test
    void cloneFailTest() {
        Assertions.assertThrows(GitAPIException.class, () -> GitUtils.cloneRepository("dabico/dl4se-crawler", tmp));
    }
}