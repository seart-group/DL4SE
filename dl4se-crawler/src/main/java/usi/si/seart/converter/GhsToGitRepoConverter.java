package usi.si.seart.converter;

import com.google.common.base.Converter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import usi.si.seart.http.payload.GhsGitRepo;
import usi.si.seart.model.GitRepo;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class GhsToGitRepoConverter extends Converter<GhsGitRepo, GitRepo> {

    @Getter
    private static final Converter<GhsGitRepo, GitRepo> instance = new GhsToGitRepoConverter();

    @Override
    protected GitRepo doForward(GhsGitRepo ghsGitRepo) {
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
        LocalDateTime lastCommit = DateToLDTConverter.getInstance().convert(ghsGitRepo.getLastCommit());
        if (lastCommit != null) builder.lastCommit(lastCommit);
        String lastCommitSHA = ghsGitRepo.getLastCommitSHA();
        if (lastCommitSHA != null) builder.lastCommitSHA(lastCommitSHA);

        return builder.build();
    }

    @Override
    protected GhsGitRepo doBackward(GitRepo repo) {
        throw new UnsupportedOperationException("Backwards conversion is not supported!");
    }
}
