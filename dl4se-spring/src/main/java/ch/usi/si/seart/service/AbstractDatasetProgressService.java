package ch.usi.si.seart.service;

import ch.usi.si.seart.exception.DatasetProgressNotFoundException;
import ch.usi.si.seart.model.dataset.Dataset;
import ch.usi.si.seart.model.dataset.DatasetProgress;
import ch.usi.si.seart.model.dataset.DatasetProgress_;
import ch.usi.si.seart.repository.DatasetProgressRepository;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.InitializingBean;

import java.time.LocalDateTime;
import java.util.Optional;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public abstract class AbstractDatasetProgressService implements DatasetProgressService, InitializingBean {

    Dataset dataset;

    LocalDateTime startDateTime;

    DatasetProgressRepository datasetProgressRepository;

    @Override
    public final DatasetProgress getProgress() {
        Optional<DatasetProgress> optional = datasetProgressRepository.findByDataset(dataset);
        return optional.orElseThrow(() -> new DatasetProgressNotFoundException(DatasetProgress_.dataset, dataset));
    }

    @Override
    public final void updateProgress(LocalDateTime checkpoint) {
        Optional<DatasetProgress> optional = datasetProgressRepository.findByDataset(dataset);
        DatasetProgress progress = optional.orElseThrow(IllegalStateException::new);
        progress.setCheckpoint(checkpoint);
        datasetProgressRepository.save(progress);
    }

    @Override
    public final void afterPropertiesSet() {
        Optional<DatasetProgress> optional = datasetProgressRepository.findByDataset(dataset);
        DatasetProgress progress = optional.orElseGet(this::initialize);
        datasetProgressRepository.save(progress);
    }

    private DatasetProgress initialize() {
        return DatasetProgress.builder()
                .checkpoint(startDateTime)
                .dataset(dataset)
                .build();
    }
}
