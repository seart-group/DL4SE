package usi.si.seart.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.Language;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    Optional<Language> findByName(String name);
}
