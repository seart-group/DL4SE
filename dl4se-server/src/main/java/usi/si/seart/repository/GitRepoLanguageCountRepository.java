package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.Language;
import usi.si.seart.views.language.GitRepoLanguageCount;

public interface GitRepoLanguageCountRepository extends JpaRepository<GitRepoLanguageCount, Language> {
}
