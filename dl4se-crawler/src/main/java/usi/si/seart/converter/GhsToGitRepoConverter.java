package usi.si.seart.converter;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usi.si.seart.http.payload.GhsGitRepo;
import usi.si.seart.model.GitRepo;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GhsToGitRepoConverter extends Converter<GhsGitRepo, GitRepo> {

    @Getter
    private static final Converter<GhsGitRepo, GitRepo> instance = new GhsToGitRepoConverter();

    @Override
    protected GitRepo forward(GhsGitRepo ghsGitRepo) {
        GitRepo.GitRepoBuilder builder = GitRepo.builder();

        builder.name(ghsGitRepo.getName());
        builder.license(ghsGitRepo.getLicense());
        builder.isFork(ghsGitRepo.getIsFork());

        Long commits = ghsGitRepo.getCommits();
        if (commits != null) builder.commits(commits);
        Long contributors = ghsGitRepo.getContributors();
        if (contributors != null) builder.contributors(contributors);
        Long issues = ghsGitRepo.getTotalIssues();
        if (issues != null) builder.issues(issues);
        Long stars = ghsGitRepo.getStargazers();
        if (stars != null) builder.stars(stars);

        builder.lastUpdate(DateToLDTConverter.getInstance().convert(ghsGitRepo.getPushedAt()));
        builder.lastCommitSHA(ghsGitRepo.getLastCommitSHA());

        return builder.build();
    }

    @Override
    protected GhsGitRepo backward(GitRepo repo) {
        throw new UnsupportedOperationException("Backwards conversion is not supported!");
    }
}
