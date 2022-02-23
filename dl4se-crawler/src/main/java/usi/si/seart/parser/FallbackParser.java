package usi.si.seart.parser;

import lombok.Getter;
import lombok.SneakyThrows;
import usi.si.seart.model.code.File;
import usi.si.seart.utils.StringUtils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FallbackParser extends AbstractParser {

    @Getter
    static FallbackParser instance = new FallbackParser();

    private FallbackParser() {
        super();
    }

    @Override
    @SneakyThrows
    public File parse(Path path) {
        String fileContents = Files.readString(path, StandardCharsets.UTF_8);
        String normalized = StringUtils.normalizeSpace(fileContents);
        fileBuilder.content(fileContents);
        fileBuilder.contentHash(StringUtils.sha256(normalized));
        fileBuilder.lines(fileContents.lines().count());
        fileBuilder.characters(fileContents.chars().count());
        fileBuilder.containsNonAscii(StringUtils.containsNonAscii(fileContents));

        return buildAll();
    }
}
