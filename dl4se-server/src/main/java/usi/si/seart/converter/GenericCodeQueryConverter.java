package usi.si.seart.converter;

import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.GenericConverter;
import usi.si.seart.dto.task.query.CodeQueryDto;
import usi.si.seart.dto.task.query.FileQueryDto;
import usi.si.seart.dto.task.query.FunctionQueryDto;
import usi.si.seart.model.task.query.CodeQuery;
import usi.si.seart.model.task.query.FileQuery;
import usi.si.seart.model.task.query.FunctionQuery;

import java.util.Set;

public class GenericCodeQueryConverter implements GenericConverter {

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Set.of(
                new ConvertiblePair(FileQueryDto.class, CodeQuery.class),
                new ConvertiblePair(FunctionQueryDto.class, CodeQuery.class)
        );
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        Class<?> sourceClass = sourceType.getType();
        if (sourceClass == FileQueryDto.class) {
            FileQueryDto dto = (FileQueryDto) source;
            FileQuery.FileQueryBuilder<?, ?> builder = FileQuery.builder();
            copyToBuilder(builder, dto);
            return builder
                    .excludeUnparsable(dto.getExcludeUnparsable())
                    .build();
        } else if (sourceClass == FunctionQueryDto.class) {
            FunctionQueryDto dto = (FunctionQueryDto) source;
            FunctionQuery.FunctionQueryBuilder<?, ?> builder = FunctionQuery.builder();
            copyToBuilder(builder, dto);
            return builder
                    .excludeBoilerplate(dto.getExcludeBoilerplate())
                    .build();
        } else {
            throw new IllegalStateException();
        }
    }

    private static void copyToBuilder(CodeQuery.CodeQueryBuilder<?, ?> builder, CodeQueryDto source) {
        builder.hasLicense(source.getHasLicense())
                .excludeForks(source.getExcludeForks())
                .minCommits(source.getMinCommits())
                .minContributors(source.getMinContributors())
                .minIssues(source.getMinIssues())
                .minStars(source.getMinStars())
                .includeAst(source.getIncludeAst())
                .minTokens(source.getMinTokens())
                .maxTokens(source.getMaxTokens())
                .minLines(source.getMinLines())
                .maxLines(source.getMaxLines())
                .minCharacters(source.getMinCharacters())
                .maxCharacters(source.getMaxCharacters())
                .excludeDuplicates(source.getExcludeDuplicates())
                .excludeIdentical(source.getExcludeIdentical())
                .excludeTest(source.getExcludeTest())
                .excludeNonAscii(source.getExcludeNonAscii());
    }
}
