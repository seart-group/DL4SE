package usi.si.seart.parser;

import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;

import java.nio.file.Path;

public interface Parser {
    File parse(Path path);

    static Parser getParser(Language language) {
        switch (language.getName()) {
            case "Java": return JavaParser.getInstance();
            default: return FallbackParser.getInstance();
        }
    }
}
