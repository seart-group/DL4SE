package usi.si.seart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import usi.si.seart.model.Configuration;

public interface ConfigurationRepository extends JpaRepository<Configuration, String> {
}
