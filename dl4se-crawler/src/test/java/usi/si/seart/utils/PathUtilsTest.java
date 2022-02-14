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
}