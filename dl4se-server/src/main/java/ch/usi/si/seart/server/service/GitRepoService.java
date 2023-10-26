package ch.usi.si.seart.server.service;

import ch.usi.si.seart.model.GitRepo;
import ch.usi.si.seart.server.repository.GitRepoRepository;
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

    void forEach(Consumer<GitRepo> consumer);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class GitRepoServiceImpl implements GitRepoService {

        GitRepoRepository gitRepoRepository;

        @Override
        @Transactional(propagation = Propagation.REQUIRES_NEW)
        public void forEach(Consumer<GitRepo> consumer) {
            @Cleanup Stream<GitRepo> repoStream = gitRepoRepository.streamAll();
            repoStream.forEach(consumer);
        }
    }
}
