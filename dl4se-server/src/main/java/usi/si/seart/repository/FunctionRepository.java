package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.Function;
import usi.si.seart.views.GroupedCount;

import java.util.Collection;

public interface FunctionRepository extends JpaRepository<Function, Long> {

    @Query("SELECT _ FROM FunctionCountByLanguage _")
    Collection<GroupedCount<Language>> countByLanguage();
}
