package usi.si.seart.analyzer;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;

import java.util.List;

public interface Analyzer extends AutoCloseable {

    Result analyze();

    @Getter
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor
    final class Result {
        File file;
        List<Function> functions;
    }
}
