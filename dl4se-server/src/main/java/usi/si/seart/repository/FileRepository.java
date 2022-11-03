package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import usi.si.seart.model.code.File;

import javax.persistence.Tuple;
import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {

    @Query("SELECT l, COUNT(l) FROM File f JOIN f.language l GROUP BY l")
    List<Tuple> countAllGroupByLanguage();
}
