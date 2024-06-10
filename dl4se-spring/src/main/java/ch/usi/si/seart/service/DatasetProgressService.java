package ch.usi.si.seart.service;

import ch.usi.si.seart.model.dataset.DatasetProgress;

import java.time.LocalDateTime;

public interface DatasetProgressService {

    DatasetProgress getProgress();
    void updateProgress(LocalDateTime checkpoint);
}
