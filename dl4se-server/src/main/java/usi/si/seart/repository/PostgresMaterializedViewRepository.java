package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import usi.si.seart.meta.PostgresMaterializedView;

import javax.validation.constraints.NotBlank;

public interface PostgresMaterializedViewRepository extends JpaRepository<PostgresMaterializedView, String> {

    @Modifying
    @Query(value = "CALL refresh_materialized_view(?1)", nativeQuery = true)
    void refreshByName(@NotBlank String name);
}
