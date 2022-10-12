package usi.si.seart.function;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.NoArgsConstructor;
import usi.si.seart.model.code.Code;

import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.function.Function;
import java.util.function.UnaryOperator;

@NoArgsConstructor
public class CodeProcessingPipeline implements Function<Code, Map<String, Object>> {

    private static final ObjectMapper objectMapper;
    static {
        objectMapper = new ObjectMapper();
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss"));
        objectMapper.registerModule(new JavaTimeModule());
    }

    private Function<Map<String, Object>, Map<String, Object>> pipeline = Function.identity();

    public void add(UnaryOperator<Map<String, Object>> operation) {
        pipeline = pipeline.andThen(operation);
    }

    @Override
    public Map<String, Object> apply(Code code) {
        Map<String, Object> map = objectMapper.convertValue(code, new TypeReference<>() {});
        return pipeline.apply(map);
    }
}
