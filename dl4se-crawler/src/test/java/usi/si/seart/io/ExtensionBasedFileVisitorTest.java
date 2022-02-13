package usi.si.seart.io;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@SuppressWarnings("ConstantConditions")
class ExtensionBasedFileVisitorTest {

    private final Path resources = Path.of(this.getClass().getResource("/code").getPath());

    /*
     * code:
     * - java: 8
     * - python: 4
     * - other: 4
     */

    @Test
    @SneakyThrows
    void noExtensionTest() {
        ExtensionBasedFileVisitor visitor = new ExtensionBasedFileVisitor();
        Files.walkFileTree(resources, visitor);
        List<Path> files = visitor.getVisited();
        Assertions.assertEquals(16, files.size());
    }

    @Test
    @SneakyThrows
    void oneExtensionTest() {
        ExtensionBasedFileVisitor visitor = new ExtensionBasedFileVisitor("java");
        Files.walkFileTree(resources, visitor);
        List<Path> files = visitor.getVisited();
        Assertions.assertEquals(8, files.size());
    }

    @Test
    @SneakyThrows
    void twoExtensionTest() {
        ExtensionBasedFileVisitor visitor = new ExtensionBasedFileVisitor("java", "py");
        Files.walkFileTree(resources, visitor);
        List<Path> files = visitor.getVisited();
        Assertions.assertEquals(12, files.size());
    }

    @Test
    void nullExtensionTest() {
        Assertions.assertThrows(NullPointerException.class, () -> new ExtensionBasedFileVisitor(null));
    }
}