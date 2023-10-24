package ch.usi.si.seart.server.converter;

import ch.usi.si.seart.model.job.Job;
import ch.usi.si.seart.model.task.Task;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.List;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class TaskToJsonMapperConverter extends TaskConverter<JsonMapper> {

    JsonMapper jsonMapper;

    @Override
    protected void validate(Task source) {
        Job dataset = source.getDataset();
        JsonNode processing = source.getProcessing();
        Assert.isTrue(Job.CODE.equals(dataset), "Can not convert this type of dataset: " + dataset);
        Assert.isTrue(processing.isObject(), "Processing must be a JSON object!");
    }

    @Override
    protected JsonMapper convertInternal(Task source) {
        JsonNode processing = source.getProcessing();
        return convert(processing);
    }

    private JsonMapper convert(JsonNode processing) {
        SimpleModule module = new SimpleModule(DynamicCodeSerializerModifier.class.getName());
        BeanSerializerModifier modifier = new DynamicCodeSerializerModifier(processing);
        module.setSerializerModifier(modifier);
        return jsonMapper.rebuild()
                .addModules(module)
                .build();
    }

    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    private static class DynamicCodeSerializerModifier extends BeanSerializerModifier {

        boolean includeAst;
        boolean includeSymbolicExpression;

        private DynamicCodeSerializerModifier(JsonNode processing) {
            this(
                    getBooleanValue(processing, "include_ast"),
                    getBooleanValue(processing, "include_symbolic_expression")
            );
        }

        private static boolean getBooleanValue(JsonNode jsonNode, String name) {
            return jsonNode.has(name) && jsonNode.get(name).asBoolean();
        }

        @Override
        public List<BeanPropertyWriter> changeProperties(
                SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties
        ) {
            if (!includeAst)
                beanProperties.removeIf(propertyWriter -> propertyWriter.getName().equals("ast"));
            if (!includeSymbolicExpression)
                beanProperties.removeIf(propertyWriter -> propertyWriter.getName().equals("symbolic_expression"));
            return beanProperties;
        }
    }
}
