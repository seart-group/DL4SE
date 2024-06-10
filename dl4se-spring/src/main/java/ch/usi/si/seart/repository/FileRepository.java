package ch.usi.si.seart.repository;

import ch.usi.si.seart.model.GitRepo;
import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.model.code.File;
import ch.usi.si.seart.views.GroupedCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Collection;

public interface FileRepository extends JpaRepository<File, Long> {

    @Query("SELECT _ FROM FileCountByLanguage _")
    Collection<GroupedCount<Language>> countByLanguage();

    @Query("SELECT path FROM File WHERE repo = :repo")
    Collection<String> findAllPathByRepo(@Param(value = "repo") @NotNull GitRepo repo);

    @Modifying
    @Query("UPDATE File SET path = :new WHERE repo = :repo AND path = :old")
    void updatePathByRepo(
            @Param(value = "repo") @NotNull GitRepo repo,
            @Param(value = "old") @NotBlank String oldPath,
            @Param(value = "new") @NotBlank String newPath
    );

    void deleteByRepoAndPath(@NotNull GitRepo repo, @NotBlank String path);
}
