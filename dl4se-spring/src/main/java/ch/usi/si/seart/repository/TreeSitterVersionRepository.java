package ch.usi.si.seart.repository;

import ch.usi.si.seart.model.meta.TreeSitterVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface TreeSitterVersionRepository extends JpaRepository<TreeSitterVersion, Long> {

    boolean existsBySha(@NotNull String sha);

    Optional<TreeSitterVersion> findBySha(@NotNull String sha);
}
