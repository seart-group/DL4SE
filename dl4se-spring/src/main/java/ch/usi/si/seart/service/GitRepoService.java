package ch.usi.si.seart.service;

import ch.usi.si.seart.exception.GitRepoNotFoundException;
import ch.usi.si.seart.model.GitRepo;
import ch.usi.si.seart.model.GitRepo_;
import ch.usi.si.seart.repository.GitRepoRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Cleanup;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.function.Consumer;
import java.util.stream.Stream;

public interface GitRepoService {

    GitRepo getByName(String name);
    GitRepo createOrUpdate(GitRepo gitRepo);
    void forEach(Consumer<GitRepo> consumer);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class GitRepoServiceImpl implements GitRepoService {

        GitRepoRepository gitRepoRepository;

        @Override
        public GitRepo getByName(String name) {
            return gitRepoRepository.findByNameIgnoreCaseFetchLanguages(name)
                    .orElseThrow(() -> new GitRepoNotFoundException(GitRepo_.name, name));
        }

        @Override
        public GitRepo createOrUpdate(GitRepo gitRepo) {
            return gitRepoRepository.save(gitRepo);
        }

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void forEach(Consumer<GitRepo> consumer) {
            @Cleanup Stream<GitRepo> repoStream = gitRepoRepository.streamAll();
            repoStream.forEach(consumer);
        }
    }
}
