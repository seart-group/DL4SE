package usi.si.seart.utils;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;

class PathUtilsTest {

    @Test
    @SneakyThrows
    void forceDeleteTest() {
        Path temp = Files.createTempDirectory("test");
        Files.createFile(Path.of(temp.toString(), "file1.txt"));
        Files.createFile(Path.of(temp.toString(), "file2.txt"));
        Files.createFile(Path.of(temp.toString(), "file3.txt"));
        Files.createDirectory(Path.of(temp.toString(), "empty"));
        Files.createFile(
                Path.of(Files.createDirectory(Path.of(temp.toString(), "nonempty")).toString(), "file4.txt")
        );
        PathUtils.forceDelete(temp);
        Assertions.assertFalse(temp.toFile().exists());
    }

    @Test
    void isTestFile() {
        Path file1 = Path.of("/java/src/org/json/App.java");
        Path file2 = Path.of("/java/src/org/json/TestApp.java");
        Path file3 = Path.of("/java/src/org/json/AppTest.java");
        Path file4 = Path.of("/java/src/org/json/AppTests.java");
        Path file5 = Path.of("/java/src/org/json/AppTestCase.java");
        Path file6 = Path.of("/java/test/org/json/App.java");
        Path file7 = Path.of("/java/tests/org/json/App.java");
        Path file8 = Path.of("/test/org/json/App.java");
        Path file9 = Path.of("/AppTest.java");
        Assertions.assertFalse(PathUtils.isTestFile(file1));
        Assertions.assertTrue(PathUtils.isTestFile(file2));
        Assertions.assertTrue(PathUtils.isTestFile(file3));
        Assertions.assertTrue(PathUtils.isTestFile(file4));
        Assertions.assertTrue(PathUtils.isTestFile(file5));
        Assertions.assertTrue(PathUtils.isTestFile(file6));
        Assertions.assertTrue(PathUtils.isTestFile(file7));
        Assertions.assertTrue(PathUtils.isTestFile(file8));
        Assertions.assertTrue(PathUtils.isTestFile(file9));
    }
}