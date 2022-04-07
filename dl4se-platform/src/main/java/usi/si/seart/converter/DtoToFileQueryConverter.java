package usi.si.seart.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.lang.NonNull;
import usi.si.seart.dto.task.query.FileQueryDto;
import usi.si.seart.model.task.query.FileQuery;

public class DtoToFileQueryConverter implements Converter<FileQueryDto, FileQuery> {

    @Override
    @NonNull
    public FileQuery convert(@NonNull FileQueryDto source) {
        return FileQuery.builder()
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
                .excludeUnparsable(source.getExcludeUnparsable())
                .build();
    }
}
