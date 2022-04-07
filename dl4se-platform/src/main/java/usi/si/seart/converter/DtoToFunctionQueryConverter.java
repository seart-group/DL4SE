package usi.si.seart.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import usi.si.seart.dto.task.query.FunctionQueryDto;
import usi.si.seart.model.task.query.FunctionQuery;

public class DtoToFunctionQueryConverter implements Converter<FunctionQueryDto, FunctionQuery> {

    @Override
    @NonNull
    public FunctionQuery convert(@NonNull FunctionQueryDto source) {
        return FunctionQuery.builder()
                .hasLicense(source.getHasLicense())
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
                .excludeNonAscii(source.getExcludeNonAscii())
                .excludeBoilerplate(source.getExcludeBoilerplate())
                .build();
    }
}
