package usi.si.seart.crawler.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.si.seart.crawler.repository.FileRepository;
import usi.si.seart.model.GitRepo;
import usi.si.seart.model.code.File;

import javax.persistence.EntityNotFoundException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

public interface FileService {

    List<File> getAllByRepo(GitRepo gitRepo);
    void create(File file);
    void delete(GitRepo gitRepo, Path path);
    void rename(GitRepo gitRepo, Path oldPath, Path newPath);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class FileServiceImpl implements FileService {

        FileRepository fileRepository;

        @Override
        public List<File> getAllByRepo(GitRepo gitRepo) {
            return fileRepository.findAllByRepo(gitRepo);
        }

        @Override
        public void create(File file) {
            fileRepository.save(file);
        }

        @Override
        public void delete(GitRepo gitRepo, Path path) {
            fileRepository.deleteByRepoAndPath(gitRepo, path.toString());
        }

        @Override
        public void rename(GitRepo gitRepo, Path oldPath, Path newPath) {
            Optional<File> optional = fileRepository.findByRepoAndPath(gitRepo, oldPath.toString());
            File file = optional.orElseThrow(EntityNotFoundException::new);
            file.setPath(newPath.toString());
            fileRepository.save(file);
        }
    }
}
