package usi.si.seart.git;

import lombok.SneakyThrows;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;

class GitUtilsTest {

    final File tmp = new File(System.getProperty("java.io.tmpdir"));

    @Test
    @SneakyThrows
    void cloneRepositoryTest() {
        File repo = GitUtils.cloneRepository("dabico/dl4se-crawler-test");
        Assertions.assertTrue(repo.exists());
        Assertions.assertTrue(repo.isDirectory());
        File[] repoFiles = repo.listFiles();
        Assertions.assertNotNull(repoFiles);
        Assertions.assertEquals(4, repoFiles.length); // README, LICENSE, .git, .gitignore
    }

    @Test
    void cloneFailTest() {
        Assertions.assertThrows(GitAPIException.class, () -> GitUtils.cloneRepository("dabico/dl4se-crawler"));
    }

    @AfterEach
    @SneakyThrows
    @SuppressWarnings({"ConstantConditions"})
    void tearDown() {
        for (File file : tmp.listFiles()) {
            if (file.isDirectory() && file.getName().startsWith("dl4se")) {
                FileUtils.deleteDirectory(file);
            }
        }
    }
}