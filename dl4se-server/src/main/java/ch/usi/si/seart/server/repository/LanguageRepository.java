package ch.usi.si.seart.server.repository;

import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.server.repository.support.ReadOnlyRepository;

import java.util.Optional;

public interface LanguageRepository extends ReadOnlyRepository<Language, Long> {

    Optional<Language> findByName(String name);
}
