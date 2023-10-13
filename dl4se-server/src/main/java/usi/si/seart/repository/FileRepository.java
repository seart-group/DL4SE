package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;
import usi.si.seart.views.GroupedCount;

import java.util.Collection;

public interface FileRepository extends JpaRepository<File, Long> {

    @Query("SELECT _ FROM FileCountByLanguage _")
    Collection<GroupedCount<Language>> countByLanguage();
}
