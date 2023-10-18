package ch.usi.si.seart.server.repository;

import ch.usi.si.seart.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    Optional<Language> findByName(String name);
}
