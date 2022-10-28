package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.code.File;

public interface FileRepository extends JpaRepository<File, Long> {
}
