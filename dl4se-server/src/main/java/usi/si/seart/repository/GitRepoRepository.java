package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import usi.si.seart.model.GitRepo;
import usi.si.seart.model.Language;
import usi.si.seart.views.GroupedCount;

import java.util.Collection;
import java.util.stream.Stream;

public interface GitRepoRepository extends JpaRepository<GitRepo, Long> {

    Stream<GitRepo> streamAllBy();

    @Query("SELECT _ FROM GitRepoCountByLanguage _")
    Collection<GroupedCount<Language>> countByLanguage();
}
