package ch.usi.si.seart.server.repository;

import ch.usi.si.seart.model.GitRepo;
import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.server.repository.support.ExtendedJpaRepository;
import ch.usi.si.seart.views.GroupedCount;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface GitRepoRepository extends ExtendedJpaRepository<GitRepo, Long> {

    @Query("SELECT _ FROM GitRepoCountByLanguage _")
    Collection<GroupedCount<Language>> countByLanguage();
}
