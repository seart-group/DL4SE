package usi.si.seart.wrapper.code;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import usi.si.seart.model.code.File;

import java.util.LinkedHashMap;
import java.util.Map;

@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AbstractedFile extends File implements Abstracted {

    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    @Builder.Default
    Map<String, String> mappings = new LinkedHashMap<>();

    public static AbstractedFile from(File file) {
        return builder().id(file.getId())
                .path(file.getPath())
                .repo(file.getRepo())
                .language(file.getLanguage())
                // FIXME 26.07.22: Getter throws LazyInitializationException
                //.functions(file.getFunctions())
                .content(file.getContent())
                .contentHash(file.getContentHash())
                .ast(file.getAst())
                .astHash(file.getAstHash())
                .totalTokens(file.getTotalTokens())
                .codeTokens(file.getCodeTokens())
                .lines(file.getLines())
                .characters(file.getCharacters())
                .isParsed(file.getIsParsed())
                .isTest(file.getIsTest())
                .containsNonAscii(file.getContainsNonAscii())
                .build();
    }
}
