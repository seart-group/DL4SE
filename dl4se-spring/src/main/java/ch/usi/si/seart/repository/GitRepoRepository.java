package ch.usi.si.seart.repository;

import ch.usi.si.seart.model.GitRepo;
import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.repository.support.JpaStreamExecutor;
import ch.usi.si.seart.views.GroupedCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Optional;

public interface GitRepoRepository extends JpaRepository<GitRepo, Long>, JpaStreamExecutor<GitRepo> {

    @Query("SELECT _ FROM GitRepoCountByLanguage _")
    Collection<GroupedCount<Language>> countByLanguage();

    @Query(
            "SELECT r FROM GitRepo r " +
            "LEFT JOIN FETCH r.languages " +
            "WHERE lower(r.name) = lower(:name)"
    )
    Optional<GitRepo> findByNameIgnoreCaseFetchLanguages(@NotNull @Param("name") String name);
}
