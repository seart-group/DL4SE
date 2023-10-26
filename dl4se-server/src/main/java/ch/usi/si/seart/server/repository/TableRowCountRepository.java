package ch.usi.si.seart.server.repository;

import ch.usi.si.seart.views.TableRowCount;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TableRowCountRepository extends JpaRepository<TableRowCount, String> {
}
