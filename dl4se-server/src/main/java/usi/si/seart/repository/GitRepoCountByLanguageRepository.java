package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.Language;
import usi.si.seart.views.language.GitRepoCountByLanguage;

public interface GitRepoCountByLanguageRepository extends JpaRepository<GitRepoCountByLanguage, Language> {
}
