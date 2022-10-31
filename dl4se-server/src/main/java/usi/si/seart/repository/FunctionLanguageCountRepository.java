package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.Language;
import usi.si.seart.views.language.FunctionLanguageCount;

public interface FunctionLanguageCountRepository extends JpaRepository<FunctionLanguageCount, Language> {
}
