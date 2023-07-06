package usi.si.seart.crawler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.Language;

public interface LanguageRepository extends JpaRepository<Language, Long> {
}
