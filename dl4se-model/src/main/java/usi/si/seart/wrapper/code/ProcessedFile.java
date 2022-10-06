package usi.si.seart.wrapper.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import usi.si.seart.model.code.File;

@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProcessedFile extends File implements Processed {

    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    String processedContent;

    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    String processedAst;

    public static ProcessedFile from(File file) {
        return builder()
                .id(file.getId())
                .path(file.getPath())
                .repo(file.getRepo())
                .language(file.getLanguage())
                // FIXME 26.07.22: Getter throws LazyInitializationException
                //.functions(file.getFunctions())
                .content(file.getContent())
                .processedContent(file.getContent())
                .contentHash(file.getContentHash())
                .ast(file.getAst())
                .processedAst(file.getAst())
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
