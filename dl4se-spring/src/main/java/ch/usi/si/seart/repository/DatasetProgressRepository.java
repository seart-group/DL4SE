package ch.usi.si.seart.repository;

import ch.usi.si.seart.model.dataset.Dataset;
import ch.usi.si.seart.model.dataset.DatasetProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.validation.constraints.NotNull;
import java.util.Optional;

public interface DatasetProgressRepository extends JpaRepository<DatasetProgress, Long> {

    Optional<DatasetProgress> findByDataset(@NotNull Dataset dataset);
}
