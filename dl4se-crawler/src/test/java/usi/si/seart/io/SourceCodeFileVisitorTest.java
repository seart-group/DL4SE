package usi.si.seart.io;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

class SourceCodeFileVisitorTest {

    Path resources = Path.of(this.getClass().getResource("/").getPath());

    @Test
    @SneakyThrows
    void noExtensionTest() {
        SourceCodeFileVisitor visitor = new SourceCodeFileVisitor();
        Files.walkFileTree(resources, visitor);
        List<File> files = visitor.getVisited();
        Assertions.assertTrue(files.size() > 16);
    }

    @Test
    @SneakyThrows
    void oneExtensionTest() {
        SourceCodeFileVisitor visitor = new SourceCodeFileVisitor("java");
        Files.walkFileTree(resources, visitor);
        List<File> files = visitor.getVisited();
        Assertions.assertEquals(8, files.size());
    }

    @Test
    @SneakyThrows
    void twoExtensionTest() {
        SourceCodeFileVisitor visitor = new SourceCodeFileVisitor("java", "py");
        Files.walkFileTree(resources, visitor);
        List<File> files = visitor.getVisited();
        Assertions.assertEquals(12, files.size());
    }
}