package ch.usi.si.seart.server.converter;

import ch.usi.si.seart.model.job.Job;
import ch.usi.si.seart.model.task.Task;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;

abstract class TaskConverter<T> implements Converter<Task, T> {

    protected void validate(@NonNull Task source) {
        Job dataset = source.getDataset();
        JsonNode query = source.getQuery();
        JsonNode processing = source.getProcessing();
        Assert.isTrue(Job.CODE.equals(dataset), "Can not convert this type of dataset: " + dataset);
        Assert.isTrue(query.isObject(), "Query must be a JSON object!");
        Assert.isTrue(processing.isObject(), "Processing must be a JSON object!");
    }

    @Override
    @NonNull
    public final T convert(@NonNull Task source) {
        validate(source);
        return convert(source.getQuery(), source.getProcessing());
    }

    protected abstract T convert(@NonNull JsonNode query, @NonNull JsonNode processing);
}
