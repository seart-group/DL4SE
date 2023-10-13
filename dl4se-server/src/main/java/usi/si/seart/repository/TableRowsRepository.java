package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.views.TableRows;

public interface TableRowsRepository extends JpaRepository<TableRows, String> {
}
