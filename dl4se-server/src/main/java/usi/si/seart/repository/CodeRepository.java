package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import usi.si.seart.model.code.Code;
import usi.si.seart.repository.specification.JpaStreamableSpecificationRepository;

public interface CodeRepository extends
        JpaRepository<Code, Long>,
        JpaSpecificationExecutor<Code>,
        JpaStreamableSpecificationRepository<Code>
{
    @Query(value = "SELECT size FROM code_size_in_bytes", nativeQuery = true)
    Long size();
}
