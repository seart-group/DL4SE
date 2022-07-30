package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.si.seart.model.task.Task;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.zip.GZIPOutputStream;

public interface FileSystemService {

    Path getTaskArchive(Task task);
    Path createTaskFile(Task task) throws IOException;
    void compressTaskFile(Task task);
    void cleanTaskFiles(Task task);

    @Slf4j
    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class FileSystemServiceImpl implements FileSystemService {

        Path fileStorageDirPath;

        @Override
        public Path getTaskArchive(Task task) {
            return getArchiveFilePath(task);
        }

        @Override
        public Path createTaskFile(Task task) throws IOException {
            Path tmpFilePath = getRegularFilePath(task);
            if (Files.notExists(tmpFilePath)) Files.createFile(tmpFilePath);
            return tmpFilePath;
        }

        @Override
        @SneakyThrows
        public void compressTaskFile(Task task) {
            Path regularFilePath = getRegularFilePath(task);
            Path archiveFilePath = getArchiveFilePath(task);
            @Cleanup GZIPOutputStream outputStream = new GZIPOutputStream(new FileOutputStream(archiveFilePath.toFile()));
            @Cleanup FileInputStream inputStream = new FileInputStream(regularFilePath.toFile());
            byte[] buffer = new byte[1024];
            int len;
            while ((len = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, len);
            }
            Files.delete(regularFilePath);
        }

        @Override
        @SneakyThrows
        public void cleanTaskFiles(Task task) {
            Path regularFilePath = getRegularFilePath(task);
            Path archiveFilePath = getArchiveFilePath(task);
            if (Files.exists(regularFilePath)) Files.delete(regularFilePath);
            if (Files.exists(archiveFilePath)) Files.delete(archiveFilePath);
        }

        private Path getRegularFilePath(Task task) {
            return getPath(task, "jsonl");
        }

        private Path getArchiveFilePath(Task task) {
            return getPath(task, "jsonl.gz");
        }

        private Path getPath(Task task, String extension) {
            String name = task.getUuid().toString();
            return Path.of(fileStorageDirPath.toString(), name+"."+extension);
        }
    }
}
