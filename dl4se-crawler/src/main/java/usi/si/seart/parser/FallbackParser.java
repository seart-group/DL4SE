package usi.si.seart.parser;

import lombok.extern.slf4j.Slf4j;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;
import usi.si.seart.utils.PathUtils;
import usi.si.seart.utils.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@Slf4j
public class FallbackParser extends AbstractParser {

    public FallbackParser(Language language) {
        super(language);
    }

    @Override
    public File parse(Path path) {
        fileBuilder.isTest(PathUtils.isTestFile(path));
        fileBuilder.language(language);

        try {
            String fileContents = Files.readString(path);
            String normalized = StringUtils.normalizeSpace(fileContents);

            fileBuilder.content(fileContents);
            fileBuilder.contentHash(StringUtils.sha256(normalized));
            fileBuilder.lines(fileContents.lines().count());
            fileBuilder.characters(fileContents.chars().count());
            fileBuilder.containsNonAscii(StringUtils.containsNonAscii(fileContents));

            return buildFileAndFunctions();
        } catch (IOException ex) {
            log.error("Could not read file: " + path, ex);
            return null;
        }
    }
}
