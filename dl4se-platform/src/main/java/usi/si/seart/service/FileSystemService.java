package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.si.seart.model.task.Task;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public interface FileSystemService {

    Path getExportFile(Task task);
    Path createExportFile(Task task) throws IOException;

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class FileSystemServiceImpl implements FileSystemService {

        Path fileStorageDirPath;

        @Override
        public Path getExportFile(Task task) {
            return getExportFilePath(task.getUuid().toString());
        }

        @Override
        public Path createExportFile(Task task) throws IOException {
            Path filePath = getExportFilePath(task.getUuid().toString());
            if (Files.notExists(filePath)) Files.createFile(filePath);
            return filePath;
        }

        private Path getExportFilePath(String name) {
            return Path.of(fileStorageDirPath.toString(), name+".jsonl");
        }
    }
}
