package ch.usi.si.seart.crawler.converter;

import ch.usi.si.seart.crawler.dto.SearchResultDto;
import ch.usi.si.seart.model.GitRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@AllArgsConstructor(onConstructor_ = @Autowired)
public class SearchResultDtoToGitRepoConverter implements Converter<SearchResultDto, GitRepo> {

    DateToLocalDateTimeConverter dateToLocalDateTimeConverter;

    @Override
    public GitRepo convert(SearchResultDto source) {
        GitRepo.GitRepoBuilder builder = GitRepo.builder();

        builder.id(source.getId());
        builder.name(source.getName());
        builder.license(source.getLicense());
        builder.isFork(source.getIsFork());

        Long commits = source.getCommits();
        if (commits != null) builder.commits(commits);
        Long contributors = source.getContributors();
        if (contributors != null) builder.contributors(contributors);
        Long issues = source.getTotalIssues();
        if (issues != null) builder.issues(issues);
        Long stars = source.getStargazers();
        if (stars != null) builder.stars(stars);
        Date lastCommit = source.getLastCommit();
        if (lastCommit != null) builder.lastCommit(dateToLocalDateTimeConverter.convert(lastCommit));
        String lastCommitSHA = source.getLastCommitSHA();
        if (lastCommitSHA != null) builder.lastCommitSHA(lastCommitSHA);

        return builder.build();
    }
}
