package usi.si.seart.wrapper.code;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import lombok.experimental.SuperBuilder;
import usi.si.seart.model.code.Function;

import java.util.LinkedHashMap;
import java.util.Map;

@SuperBuilder
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AbstractedFunction extends Function implements Abstracted {

    @Getter(onMethod_ = @Override)
    @Setter(onMethod_ = @Override)
    @Builder.Default
    Map<String, String> mappings = new LinkedHashMap<>();

    public static AbstractedFunction from(Function function) {
        return builder().id(function.getId())
                .file(function.getFile())
                .repo(function.getRepo())
                .language(function.getLanguage())
                .content(function.getContent())
                .contentHash(function.getContentHash())
                .ast(function.getAst())
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
