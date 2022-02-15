package usi.si.seart.parser;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import usi.si.seart.model.code.File;

import java.nio.file.Path;

@FieldDefaults(level = AccessLevel.PROTECTED, makeFinal = true)
public abstract class AbstractParser implements Parser {

    Path clonePath;
    File file = new File();

    protected AbstractParser(Path clonePath) {
        this.clonePath = clonePath;
    }
}
