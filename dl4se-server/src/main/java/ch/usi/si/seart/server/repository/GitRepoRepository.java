package ch.usi.si.seart.server.repository;

import ch.usi.si.seart.model.GitRepo;
import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.views.GroupedCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.stream.Stream;

public interface GitRepoRepository extends JpaRepository<GitRepo, Long> {

    Stream<GitRepo> streamAllBy();

    @Query("SELECT _ FROM GitRepoCountByLanguage _")
    Collection<GroupedCount<Language>> countByLanguage();
}
