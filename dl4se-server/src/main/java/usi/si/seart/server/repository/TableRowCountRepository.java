package usi.si.seart.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.views.TableRowCount;

public interface TableRowCountRepository extends JpaRepository<TableRowCount, String> {
}
