package usi.si.seart.converter;

import lombok.experimental.UtilityClass;
import usi.si.seart.dto.task.query.CodeQueryDto;
import usi.si.seart.model.task.query.CodeQuery;

@UtilityClass
public class ConverterUtil {

    void copyToBuilder(CodeQuery.CodeQueryBuilder<?, ?> builder, CodeQueryDto source) {
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
                .excludeNonAscii(source.getExcludeNonAscii());
    }
}
