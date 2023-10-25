package ch.usi.si.seart.server.repository;

import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.model.code.Function;
import ch.usi.si.seart.server.repository.support.ExtendedJpaRepository;
import ch.usi.si.seart.views.GroupedCount;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface FunctionRepository extends ExtendedJpaRepository<Function, Long> {

    @Query("SELECT _ FROM FunctionCountByLanguage _")
    Collection<GroupedCount<Language>> countByLanguage();
}
