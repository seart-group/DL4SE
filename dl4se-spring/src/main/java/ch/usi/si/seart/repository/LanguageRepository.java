package ch.usi.si.seart.repository;

import ch.usi.si.seart.model.Language;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface LanguageRepository extends JpaRepository<Language, Long> {

    Optional<Language> findByNameIgnoreCase(@NotBlank String name);

    List<Language> findAllByNameIn(Collection<@NotBlank String> name);
}
