package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.GitRepo;

import java.util.stream.Stream;

public interface GitRepoRepository extends JpaRepository<GitRepo, Long> {

    Stream<GitRepo> streamAllBy();
}
