package ch.usi.si.seart.git;

import ch.usi.si.seart.crawler.git.Git;
import ch.usi.si.seart.crawler.git.GitException;
import ch.usi.si.seart.model.Language;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
class GitTest {

    @NonFinal
    @TempDir
    Path tmp;
    // https://github.com/dabico/dl4se-test
    String testRepoName = "dabico/dl4se-test";
    // https://github.com/dabico/dl4se-empty
    String emptyRepoName = "dabico/dl4se-empty";
    // https://github.com/dabico/dl4se-history
    String historyRepoName = "dabico/dl4se-history";

    Language java = Language.builder().extensions(Collections.singletonList("java")).build();
    Language python = Language.builder().extensions(Collections.singletonList("py")).build();
    Language cpp = Language.builder()
            .extensions(List.of(
                    "c",
                    "cc",
                    "cpp",
                    "cxx"
            ))
            .build();

    @Test
    void regularCloneTest() throws IOException, GitException {
        try (Git ignored = new Git(testRepoName, tmp)) {
            checkContents(tmp.toFile());
        }
    }

    @Test
    void shallowCloneTest() throws IOException, GitException {
        try (Git ignored = new Git(testRepoName, tmp, true)) {
            checkContents(tmp.toFile());
        }
    }

    @Test
    void shallowCloneSinceTest() throws IOException, GitException {
        try (Git ignored = new Git(testRepoName, tmp, LocalDateTime.of(2022, 2, 12, 0, 0))) {
            checkContents(tmp.toFile());
        }
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
    void getLastCommitInfoTest() throws IOException, GitException {
        try (Git git = new Git(testRepoName, tmp, true)) {
            Git.Commit commit = git.getLastCommitInfo();
            Assertions.assertEquals("010e305c9818d7d8a985e91cf60739ac3b66d24e", commit.getSha());
            Assertions.assertEquals(LocalDateTime.of(2022, 2, 12, 20, 19, 51), commit.getTimestamp());
        }
    }

    @Test
    void getLastCommitEmptyRepoTest() throws IOException, GitException {
        try (Git git = new Git(emptyRepoName, tmp)) {
            Assertions.assertThrows(GitException.class, git::getLastCommitInfo);
        }
    }

    @Test
    void getDiffLowerBoundTest() throws IOException, GitException {
        try (Git git = new Git(historyRepoName, tmp)) {
            Git.Diff diff = git.getDiff("bc74b0bbd2821c4cdb0b1943f6b3afced8d49ca7");
            // Expected diff output:
            // M       .gitignore
            // D       app.cpp
            // A       app.py
            // R079    app.java       dir/app.java
            // R100    app.scala      dir/app.scala
            // R100    app.c          program.c
            Assertions.assertEquals(1, diff.getAdded().size());
            Assertions.assertEquals(1, diff.getDeleted().size());
            Assertions.assertEquals(1, diff.getModified().size());
            Assertions.assertEquals(2, diff.getRenamed().size());
            Assertions.assertEquals(1, diff.getEdited().size());
        }
    }

    @Test
    void getDiffTestLowerAndUpperBoundTest() throws IOException, GitException {
        try (Git git = new Git(historyRepoName, tmp)) {
            Git.Diff diff = git.getDiff(
                    "bc74b0bbd2821c4cdb0b1943f6b3afced8d49ca7",
                    "5dae826d5335bc633ce4bbae74e6b8394e563c13"
            );
            // Expected diff output:
            // D       app.cpp
            // A       app.py
            // R100    app.c      program.c
            Assertions.assertEquals(1, diff.getDeleted().size());
            Assertions.assertEquals(1, diff.getAdded().size());
            Assertions.assertEquals(0, diff.getModified().size());
            Assertions.assertEquals(1, diff.getRenamed().size());
            Assertions.assertEquals(0, diff.getEdited().size());
            diff = git.getDiff(
                    "db7dfcf4f141ffbf34c9acd089087e493029a973",
                    "225c820cb9ba921127cfc57ee358e1205efd06c9"
            );
            // Expected diff output:
            // R079    app.java    dir/app.java
            // R100    app.c       program.c
            Assertions.assertEquals(0, diff.getDeleted().size());
            Assertions.assertEquals(0, diff.getAdded().size());
            Assertions.assertEquals(0, diff.getModified().size());
            Assertions.assertEquals(1, diff.getRenamed().size());
            Assertions.assertEquals(1, diff.getEdited().size());
        }
    }

    @Test
    void getDiffLowerBoundFilteredTest() throws IOException, GitException {
        try (Git git = new Git(historyRepoName, tmp)) {
            Git.Diff diff = git.getDiff("bc74b0bbd2821c4cdb0b1943f6b3afced8d49ca7", Set.of(java, python));
            // Expected diff output:
            // A       app.py
            // R079    app.java    dir/app.java
            Assertions.assertEquals(1, diff.getAdded().size());
            Assertions.assertEquals(0, diff.getDeleted().size());
            Assertions.assertEquals(0, diff.getModified().size());
            Assertions.assertEquals(0, diff.getRenamed().size());
            Assertions.assertEquals(1, diff.getEdited().size());

            diff = git.getDiff("bc74b0bbd2821c4cdb0b1943f6b3afced8d49ca7", Set.of(cpp, python));
            // Expected diff output:
            // D       app.cpp
            // A       app.py
            // R100    app.c      program.c
            Assertions.assertEquals(1, diff.getAdded().size());
            Assertions.assertEquals(1, diff.getDeleted().size());
            Assertions.assertEquals(0, diff.getModified().size());
            Assertions.assertEquals(1, diff.getRenamed().size());
            Assertions.assertEquals(0, diff.getEdited().size());
        }
    }

    @Test
    void getDiffTestLowerAndUpperBoundFilteredTest() throws IOException, GitException {
        try (Git git = new Git(historyRepoName, tmp)) {
            Git.Diff diff = git.getDiff(
                    "bc74b0bbd2821c4cdb0b1943f6b3afced8d49ca7",
                    "5dae826d5335bc633ce4bbae74e6b8394e563c13",
                    Set.of(java, python)
            );
            // Expected diff output:
            // A    app.py
            Assertions.assertEquals(1, diff.getAdded().size());
            Assertions.assertEquals(0, diff.getDeleted().size());
            Assertions.assertEquals(0, diff.getModified().size());
            Assertions.assertEquals(0, diff.getRenamed().size());
            Assertions.assertEquals(0, diff.getEdited().size());

            diff = git.getDiff(
                    "bc74b0bbd2821c4cdb0b1943f6b3afced8d49ca7",
                    "5dae826d5335bc633ce4bbae74e6b8394e563c13",
                    Set.of(cpp, python)
            );
            // Expected diff output:
            // D       app.cpp
            // A       app.py
            // R100    app.c      program.c
            Assertions.assertEquals(1, diff.getAdded().size());
            Assertions.assertEquals(1, diff.getDeleted().size());
            Assertions.assertEquals(0, diff.getModified().size());
            Assertions.assertEquals(1, diff.getRenamed().size());
            Assertions.assertEquals(0, diff.getEdited().size());

            diff = git.getDiff(
                    "bc74b0bbd2821c4cdb0b1943f6b3afced8d49ca7",
                    "5dae826d5335bc633ce4bbae74e6b8394e563c13",
                    Set.of(java)
            );
            // Expected diff output:
            //
            Assertions.assertEquals(0, diff.getAdded().size());
            Assertions.assertEquals(0, diff.getDeleted().size());
            Assertions.assertEquals(0, diff.getModified().size());
            Assertions.assertEquals(0, diff.getRenamed().size());
            Assertions.assertEquals(0, diff.getEdited().size());
        }
    }

    @Test
    void getDiffSameSHATest() throws IOException, GitException {
        try (Git git = new Git(historyRepoName, tmp)) {
            String lastCommitSha = "bc74b0bbd2821c4cdb0b1943f6b3afced8d49ca7";
            Git.Diff diff = git.getDiff(lastCommitSha, lastCommitSha);
            Assertions.assertEquals(0, diff.getAdded().size());
            Assertions.assertEquals(0, diff.getDeleted().size());
            Assertions.assertEquals(0, diff.getModified().size());
            Assertions.assertEquals(0, diff.getRenamed().size());
            Assertions.assertEquals(0, diff.getEdited().size());
        }
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {
            "0000000",
            "0000000000000000000000000000000000000000"
    })
    void gitDiffBadSHATest(String sha) throws IOException, GitException {
        try (Git git = new Git(testRepoName, tmp)) {
            Assertions.assertThrows(GitException.class, () -> git.getDiff(sha));
        }
    }

    @Test
    @SuppressWarnings("resource")
    void nonEmptyDirTest() throws IOException {
        File newFile = new File(tmp.toFile().getAbsolutePath() + File.separator + "empty_file.txt");
        boolean created = newFile.createNewFile();
        Assertions.assertTrue(created);
        Assertions.assertThrows(GitException.class, () -> new Git(testRepoName, tmp, true));
    }

    @Test
    @SuppressWarnings("resource")
    void nonExistingRepoTest() {
        String fakeRepoName = "dabico/fake-repo";
        Assertions.assertThrows(GitException.class, () -> new Git(fakeRepoName, tmp, false));
    }

    @Test
    @SuppressWarnings("resource")
    void invalidShallowDateTest() {
        Assertions.assertThrows(
                GitException.class, () -> new Git(testRepoName, tmp, LocalDateTime.of(2022, 2, 14, 0, 0))
        );
    }
}
