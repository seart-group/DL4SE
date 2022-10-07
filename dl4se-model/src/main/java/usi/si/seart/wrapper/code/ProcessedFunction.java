package usi.si.seart.wrapper.code;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import usi.si.seart.model.code.Function;

@SuperBuilder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ProcessedFunction extends Function implements Processed {

    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    String processedContent;

    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    String processedAst;

    public static ProcessedFunction from(Function function) {
        return builder()
                .id(function.getId())
                .file(function.getFile())
                .repo(function.getRepo())
                .language(function.getLanguage())
                .content(function.getContent())
                .processedContent(function.getContent())
                .contentHash(function.getContentHash())
                .ast(function.getAst())
                .processedAst(function.getAst())
                .astHash(function.getAstHash())
                .totalTokens(function.getTotalTokens())
                .codeTokens(function.getCodeTokens())
                .lines(function.getLines())
                .characters(function.getCharacters())
                .isTest(function.getIsTest())
                .containsNonAscii(function.getContainsNonAscii())
                .boilerplateType(function.getBoilerplateType())
                .build();
    }
}
