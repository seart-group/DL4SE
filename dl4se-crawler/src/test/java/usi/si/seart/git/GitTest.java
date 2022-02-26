package usi.si.seart.git;

import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class GitTest {

    @NonFinal
    @TempDir
    Path tmp;
    // https://github.com/dabico/dl4se-test
    String testRepoName = "dabico/dl4se-test";

    @Test
    @SneakyThrows({GitException.class})
    void regularCloneTest() {
        new Git(testRepoName, tmp);
        checkContents(tmp.toFile());
    }

    @Test
    @SneakyThrows({GitException.class})
    void shallowCloneTest() {
        new Git(testRepoName, tmp, true);
        checkContents(tmp.toFile());
    }

    @Test
    @SneakyThrows({GitException.class})
    void shallowCloneSinceTest() {
        new Git(testRepoName, tmp, LocalDateTime.of(2022, 2, 12, 0, 0));
        checkContents(tmp.toFile());
    }

    private void checkContents(File dir) {
        Assertions.assertTrue(dir.exists());
        Assertions.assertTrue(dir.isDirectory());
        File[] files = dir.listFiles();
        Assertions.assertNotNull(files);
        Assertions.assertEquals(4, files.length);
    }

    // ref: https://github.com/dabico/dl4se-crawler-test/commit/010e305c9818d7d8a985e91cf60739ac3b66d24e
    @Test
    @SneakyThrows({GitException.class})
    void getLastCommitInfoTest() {
        Git git = new Git(testRepoName, tmp, true);
        Git.Commit commit = git.getLastCommitInfo();
        Assertions.assertEquals("010e305c9818d7d8a985e91cf60739ac3b66d24e", commit.getSha());
        Assertions.assertEquals(LocalDateTime.of(2022, 2, 12, 20, 19, 51), commit.getLastUpdate());
    }

    @Test
    @SneakyThrows({GitException.class})
    void getLastCommitEmptyRepoTest() {
        // https://github.com/dabico/dl4se-empty
        String emptyRepoName = "dabico/dl4se-empty";
        Git git = new Git(emptyRepoName, tmp);
        Assertions.assertThrows(GitException.class, git::getLastCommitInfo);
    }

    @Test
    @SneakyThrows({IOException.class})
    void nonEmptyDirTest() {
        File newFile = new File(tmp.toFile().getAbsolutePath() + File.separator + "empty_file.txt");
        boolean created = newFile.createNewFile();
        Assertions.assertTrue(created);
        Assertions.assertThrows(GitException.class, () -> new Git(testRepoName, tmp, true));
    }

    @Test
    void nonExistingRepoTest() {
        String fakeRepoName = "dabico/fake-repo";
        Assertions.assertThrows(GitException.class, () -> new Git(fakeRepoName, tmp, false));
    }

    @Test
    void invalidShallowDateTest() {
        Assertions.assertThrows(
                GitException.class, () -> new Git(testRepoName, tmp, LocalDateTime.of(2022, 2, 14, 0, 0))
        );
    }
}