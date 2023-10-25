package ch.usi.si.seart.server.repository;

import ch.usi.si.seart.model.code.Code;
import ch.usi.si.seart.server.repository.support.ExtendedJpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CodeRepository extends ExtendedJpaRepository<Code, Long> {

    @Query(value = "SELECT size FROM total_code_size_in_bytes", nativeQuery = true)
    Long size();
}
