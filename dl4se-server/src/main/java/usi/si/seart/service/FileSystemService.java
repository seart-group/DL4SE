package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.si.seart.model.task.Task;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface FileSystemService {

    Path getTaskArchive(Task task);
    Path createTaskArchive(Task task);
    void cleanTaskFiles(Task task);
    Long getFileSize(Task task);

    @Slf4j
    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class FileSystemServiceImpl implements FileSystemService {

        Path fileStorageDirPath;

        @Override
        public Path getTaskArchive(Task task) {
            return getPath(task);
        }

        @Override
        public Path createTaskArchive(Task task) {
            Path path = getPath(task);
            if (Files.exists(path)) return path;
            try {
                return Files.createFile(path);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        @Override
        public void cleanTaskFiles(Task task) {
            Path path = getPath(task);
            if (Files.notExists(path)) return;
            try {
                Files.delete(path);
            } catch (IOException ex) {
                throw new UncheckedIOException(ex);
            }
        }

        @Override
        public Long getFileSize(Task task) {
            Path path = getPath(task);
            File file = path.toFile();
            return file.length();
        }

        private Path getPath(Task task) {
            String name = task.getUuid().toString();
            return Path.of(fileStorageDirPath.toString(), name + ".jsonl.gz");
        }
    }
}
