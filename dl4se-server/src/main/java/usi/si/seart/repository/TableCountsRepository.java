package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.views.TableCount;

public interface TableCountsRepository extends JpaRepository<TableCount, String> {
}
