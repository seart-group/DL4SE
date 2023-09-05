package usi.si.seart.crawler.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.Language;

import javax.validation.constraints.NotBlank;
import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    Optional<Language> findByNameIgnoreCase(@NotBlank String name);
}
