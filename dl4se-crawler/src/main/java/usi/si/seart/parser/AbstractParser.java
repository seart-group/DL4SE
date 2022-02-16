package usi.si.seart.parser;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractParser implements Parser {

    Path clonePath;
    File.FileBuilder<?, ?> fileBuilder = File.builder();
    List<Function.FunctionBuilder<?, ?>> functionBuilders = new ArrayList<>();

    protected AbstractParser(Path clonePath) {
        this.clonePath = clonePath;
    }

    protected File buildAll() {
        File file = fileBuilder.build();
        List<Function> functions = functionBuilders.stream()
                .map(builder -> builder.file(file).isTest(file.getIsTest()).build())
                .collect(Collectors.toList());
        file.setFunctions(functions);
        return file;
    }
}
