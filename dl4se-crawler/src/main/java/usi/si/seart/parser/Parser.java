package usi.si.seart.parser;

import usi.si.seart.model.code.File;

import java.nio.file.Path;

public interface Parser {
    File parse(Path path);
}
