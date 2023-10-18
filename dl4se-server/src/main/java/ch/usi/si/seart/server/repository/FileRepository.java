package ch.usi.si.seart.server.repository;

import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.model.code.File;
import ch.usi.si.seart.views.GroupedCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;

public interface FileRepository extends JpaRepository<File, Long> {

    @Query("SELECT _ FROM FileCountByLanguage _")
    Collection<GroupedCount<Language>> countByLanguage();
}
