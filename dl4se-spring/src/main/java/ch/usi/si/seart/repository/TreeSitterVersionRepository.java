package ch.usi.si.seart.repository;

import ch.usi.si.seart.model.meta.TreeSitterVersion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TreeSitterVersionRepository extends JpaRepository<TreeSitterVersion, Long> {
}
