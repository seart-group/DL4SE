package usi.si.seart;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import usi.si.seart.git.GitUtils;
import usi.si.seart.io.ExtensionBasedFileVisitor;
import usi.si.seart.model.code.File;
import usi.si.seart.parser.JavaParser;
import usi.si.seart.parser.Parser;
import usi.si.seart.utils.PathUtils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Slf4j
public class Crawler {
    @SneakyThrows
    public static void main(String[] args) {
        Path clone = GitUtils.cloneRepository(args[0]);
        ExtensionBasedFileVisitor visitor = new ExtensionBasedFileVisitor("java");
        Files.walkFileTree(clone, visitor);
        List<Path> paths = visitor.getVisited();
        for (Path path : paths) {
            Parser parser = new JavaParser(clone);
            File file = parser.parse(path);
            log.info(file.toString());
        }
        PathUtils.forceDelete(clone);
    }
}
