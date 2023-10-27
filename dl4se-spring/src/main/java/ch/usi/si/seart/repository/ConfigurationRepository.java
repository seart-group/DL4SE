package ch.usi.si.seart.repository;

import ch.usi.si.seart.model.Configuration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfigurationRepository extends JpaRepository<Configuration, String> {
}
