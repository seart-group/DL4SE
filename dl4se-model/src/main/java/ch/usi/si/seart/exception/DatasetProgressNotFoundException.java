package ch.usi.si.seart.exception;

import ch.usi.si.seart.model.dataset.DatasetProgress;

import javax.persistence.metamodel.Attribute;

public class DatasetProgressNotFoundException extends EntityNotFoundException {

    public <T> DatasetProgressNotFoundException(Attribute<DatasetProgress, T> attribute, T value) {
        super(DatasetProgress.class, attribute, value);
    }
}
