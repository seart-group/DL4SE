package ch.usi.si.seart.crawler.service;

import ch.usi.si.seart.crawler.repository.GitRepoRepository;
import ch.usi.si.seart.model.GitRepo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;

public interface GitRepoService {

    GitRepo getByName(String name);
    GitRepo createOrUpdate(GitRepo gitRepo);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class GitRepoServiceImpl implements GitRepoService {

        GitRepoRepository gitRepoRepository;

        @Override
        public GitRepo getByName(String name) {
            return gitRepoRepository.findByNameIgnoreCaseFetchLanguages(name).orElseThrow(EntityNotFoundException::new);
        }

        @Override
        public GitRepo createOrUpdate(GitRepo gitRepo) {
            return gitRepoRepository.save(gitRepo);
        }
    }
}
