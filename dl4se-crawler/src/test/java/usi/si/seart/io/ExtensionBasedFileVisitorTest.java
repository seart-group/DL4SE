package usi.si.seart.io;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("ConstantConditions")
class ExtensionBasedFileVisitorTest {

    Path resources = Path.of(this.getClass().getResource("/").getPath());

    @Test
    @SneakyThrows
    void noExtensionTest() {
        ExtensionBasedFileVisitor visitor = new ExtensionBasedFileVisitor();
        Files.walkFileTree(resources, visitor);
        List<File> files = visitor.getVisited();
        Assertions.assertTrue(files.size() > 16);
    }

    @Test
    @SneakyThrows
    void oneExtensionTest() {
        ExtensionBasedFileVisitor visitor = new ExtensionBasedFileVisitor("java");
        Files.walkFileTree(resources, visitor);
        List<File> files = visitor.getVisited();
        Assertions.assertEquals(8, files.size());
    }

    @Test
    @SneakyThrows
    void twoExtensionTest() {
        ExtensionBasedFileVisitor visitor = new ExtensionBasedFileVisitor("java", "py");
        Files.walkFileTree(resources, visitor);
        List<File> files = visitor.getVisited();
        Assertions.assertEquals(12, files.size());
    }
}