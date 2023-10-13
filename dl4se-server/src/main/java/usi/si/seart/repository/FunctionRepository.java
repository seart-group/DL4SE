package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.code.Function;

public interface FunctionRepository extends JpaRepository<Function, Long> {
}
