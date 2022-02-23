package usi.si.seart.parser;

import lombok.SneakyThrows;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;
import usi.si.seart.utils.PathUtils;
import usi.si.seart.utils.StringUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FallbackParser extends AbstractParser {

    public FallbackParser(Language language) {
        super(language);
    }

    @Override
    @SneakyThrows({IOException.class})
    public File parse(Path path) {
        fileBuilder.isTest(PathUtils.isTestFile(path));
        fileBuilder.language(language);

        String fileContents = Files.readString(path, StandardCharsets.UTF_8);
        String normalized = StringUtils.normalizeSpace(fileContents); // TODO: Should we do this?
        fileBuilder.content(fileContents);
        fileBuilder.contentHash(StringUtils.sha256(normalized));
        fileBuilder.lines(fileContents.lines().count());
        fileBuilder.characters(fileContents.chars().count());
        fileBuilder.containsNonAscii(StringUtils.containsNonAscii(fileContents));

        return buildFileAndFunctions();
    }
}
