package usi.si.seart.parser;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@FieldDefaults(level = AccessLevel.PROTECTED)
public abstract class AbstractParser implements Parser {

    File.FileBuilder<?, ?> fileBuilder = File.builder();
    List<Function.FunctionBuilder<?, ?>> functionBuilders = new ArrayList<>();

    Language language;

    protected AbstractParser(Language language) {
        this.language = language;
    }

    protected File buildFileAndFunctions() {
        File file = fileBuilder.build();
        List<Function> functions = functionBuilders.stream()
                .map(builder -> builder.file(file).isTest(file.getIsTest()).build())
                .collect(Collectors.toList());
        file.setFunctions(functions);
        return file;
    }
}
