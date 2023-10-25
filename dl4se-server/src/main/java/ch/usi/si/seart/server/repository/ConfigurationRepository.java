package ch.usi.si.seart.server.repository;

import ch.usi.si.seart.model.Configuration;
import ch.usi.si.seart.server.repository.support.ExtendedJpaRepository;

public interface ConfigurationRepository extends ExtendedJpaRepository<Configuration, String> {
}
