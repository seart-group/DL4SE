package usi.si.seart.utils;

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
        GitUtils.cloneRepository("dabico/dl4se-crawler-test", tmp);
        File directory = tmp.toFile();
        Assertions.assertTrue(directory.exists());
        Assertions.assertTrue(directory.isDirectory());
        File[] repoFiles = directory.listFiles();
        Assertions.assertNotNull(repoFiles);
        Assertions.assertEquals(4, repoFiles.length); // README, LICENSE, .git, .gitignore
    }

    @Test
    void cloneFailTest() {
        Assertions.assertThrows(GitAPIException.class, () -> GitUtils.cloneRepository("dabico/dl4se-crawler", tmp));
    }
}