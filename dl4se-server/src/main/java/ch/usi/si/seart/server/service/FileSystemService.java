package ch.usi.si.seart.server.service;

import ch.usi.si.seart.model.task.Task;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface FileSystemService {

    Path getArchive(Task task);
    Path createArchive(Task task);
    void cleanArchive(Task task);
    Long getArchiveSize(Task task);

    @Slf4j
    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class FileSystemServiceImpl implements FileSystemService, InitializingBean {

        Path basedir;

        @Override
        public Path getArchive(Task task) {
            return getPath(task);
        }

        @Override
        public Path createArchive(Task task) {
            Path path = getPath(task);
            if (Files.exists(path)) return path;
            try {
                return Files.createFile(path);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        @Override
        public void cleanArchive(Task task) {
            Path path = getPath(task);
            if (Files.notExists(path)) return;
            try {
                Files.delete(path);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        @Override
        public Long getArchiveSize(Task task) {
            Path path = getPath(task);
            File file = path.toFile();
            return file.length();
        }

        private Path getPath(Task task) {
            return Path.of(basedir.toString(), task.getUuid() + ".jsonl.gz");
        }

        @Override
        public void afterPropertiesSet() throws IOException {
            try {
                Files.createDirectory(basedir);
            } catch (FileAlreadyExistsException ignored) {
            } finally {
                log.info("File storage directory: '{}'", basedir);
            }
        }
    }
}
