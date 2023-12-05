package ch.usi.si.seart.server.converter;

import ch.usi.si.seart.model.dataset.Dataset;
import ch.usi.si.seart.model.task.Task;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

abstract class TaskConverter<T> implements Converter<Task, T> {

    protected void validate(@NonNull Task source) {
        Dataset dataset = source.getDataset();
        JsonNode query = source.getQuery();
        JsonNode processing = source.getProcessing();
        Assert.isTrue(Dataset.CODE.equals(dataset), "Can not convert this type of dataset: " + dataset);
        Assert.isTrue(query.isObject(), "Query must be a JSON object!");
        Assert.isTrue(processing.isObject(), "Processing must be a JSON object!");
    }

    @Override
    @NonNull
    public T convert(@NonNull Task source) {
        validate(source);
        return convertInternal(source);
    }

    protected abstract T convertInternal(Task source);
}
