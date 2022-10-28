package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import usi.si.seart.model.code.Function;

import javax.persistence.Tuple;
import java.util.List;

public interface FunctionRepository extends JpaRepository<Function, Long> {

    @Query("SELECT l, COUNT(l) FROM Function f JOIN f.language l GROUP BY l")
    List<Tuple> countAllGroupByLanguage();
}
