package ch.usi.si.seart.crawler.repository;

import ch.usi.si.seart.model.GitRepo;
import ch.usi.si.seart.model.code.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    List<File> findAllByRepo(@NotNull GitRepo repo);

    @Modifying
    @Query("UPDATE File SET path = :new WHERE repo = :repo AND path = :old")
    void updatePathByRepo(
            @Param(value = "repo") @NotNull GitRepo repo,
            @Param(value = "old") @NotBlank String oldPath,
            @Param(value = "new") @NotBlank String newPath
    );

    void deleteByRepoAndPath(@NotNull GitRepo repo, @NotBlank String path);
}
