package usi.si.seart.crawler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.GitRepo;
import usi.si.seart.model.code.File;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findAllByRepo(@NotNull GitRepo repo);

    Optional<File> findByRepoAndPath(@NotNull GitRepo repo, @NotBlank String path);
}
