package ch.usi.si.seart.crawler.repository;

import ch.usi.si.seart.model.GitRepo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface GitRepoRepository extends JpaRepository<GitRepo, Long>, JpaSpecificationExecutor<GitRepo> {

    @Query(
            "SELECT r FROM GitRepo r " +
            "LEFT JOIN FETCH r.languages " +
            "WHERE lower(r.name) = lower(:name)"
    )
    Optional<GitRepo> findByNameIgnoreCaseFetchLanguages(@NotNull @Param("name") String name);
}
