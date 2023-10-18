package usi.si.seart.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import usi.si.seart.model.code.Code;
import usi.si.seart.server.repository.specification.JpaStreamableSpecificationRepository;

public interface CodeRepository extends
        JpaRepository<Code, Long>,
        JpaSpecificationExecutor<Code>,
        JpaStreamableSpecificationRepository<Code>
{
    @Query(value = "SELECT size FROM total_code_size_in_bytes", nativeQuery = true)
    Long size();
}
