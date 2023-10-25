package ch.usi.si.seart.server.repository;

import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.server.repository.support.ExtendedJpaRepository;

import java.util.Optional;

public interface LanguageRepository extends ExtendedJpaRepository<Language, Long> {

    Optional<Language> findByName(String name);
}
