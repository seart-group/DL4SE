package usi.si.seart.function;

import lombok.NoArgsConstructor;
import usi.si.seart.model.code.Code;

import java.util.function.Function;

@NoArgsConstructor
public class CodeProcessingPipeline implements Function<Code, Code> {

    Function<Code, Code> pipeline = Function.identity();

    public void add(Function<Code, Code> operation) {
        pipeline = pipeline.andThen(operation);
    }

    @Override
    public Code apply(Code code) {
        return pipeline.apply(code);
    }
}
