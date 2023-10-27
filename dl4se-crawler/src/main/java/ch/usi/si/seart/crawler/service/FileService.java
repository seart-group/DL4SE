package ch.usi.si.seart.crawler.service;

import ch.usi.si.seart.model.GitRepo;
import ch.usi.si.seart.model.code.File;
import ch.usi.si.seart.repository.FileRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Path;
import java.util.Set;
import java.util.stream.Collectors;

public interface FileService {

    Set<Path> getAllPathsByRepo(GitRepo gitRepo);
    void create(File file);
    void delete(GitRepo gitRepo, Path path);
    void rename(GitRepo gitRepo, Path oldPath, Path newPath);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class FileServiceImpl implements FileService {

        FileRepository fileRepository;

        @Override
        public Set<Path> getAllPathsByRepo(GitRepo gitRepo) {
            return fileRepository.findAllPathByRepo(gitRepo)
                    .stream()
                    .map(Path::of)
                    .collect(Collectors.toSet());
        }

        @Override
        public void create(File file) {
            fileRepository.save(file);
        }

        @Override
        @Transactional
        public void delete(GitRepo gitRepo, Path path) {
            fileRepository.deleteByRepoAndPath(gitRepo, path.toString());
        }

        @Override
        @Transactional
        public void rename(GitRepo gitRepo, Path oldPath, Path newPath) {
            fileRepository.updatePathByRepo(gitRepo, oldPath.toString(), newPath.toString());
        }
    }
}
