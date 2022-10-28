package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import usi.si.seart.model.GitRepo;

import javax.persistence.Tuple;
import java.util.List;
import java.util.stream.Stream;

public interface GitRepoRepository extends JpaRepository<GitRepo, Long> {

    Stream<GitRepo> streamAllBy();

    @Query("SELECT l, COUNT(r) FROM GitRepo r JOIN r.languages l GROUP BY l")
    List<Tuple> countAllGroupByLanguage();
}
