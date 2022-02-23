package usi.si.seart.http.payload;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import usi.si.seart.model.GitRepo;

import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GhsGitRepo {
    Long id;
    String name;
    Boolean isFork;
    Long commits;
    Long branches;
    String defaultBranch;
    Long releases;
    Long contributors;
    String license;
    Long watchers;
    Long stargazers;
    Long forks;
    Long size;
    Date createdAt;
    Date pushedAt;
    Date updatedAt;
    String homepage;
    String mainLanguage;
    Long totalIssues;
    Long openIssues;
    Long totalPullRequests;
    Long openPullRequests;
    Date lastCommit;
    String lastCommitSHA;
    Boolean hasWiki;
    Boolean isArchived;
    Map<String, Long> languages;
    Set<String> labels;

    public GitRepo.GitRepoBuilder toGitRepoBuilder() {
        GitRepo.GitRepoBuilder builder = GitRepo.builder();
        builder.name(name);
        builder.license(license);
        builder.isFork(isFork);
        if (commits != null) builder.commits(commits);
        if (contributors != null) builder.contributors(contributors);
        if (totalIssues != null) builder.issues(totalIssues);
        if (stargazers != null) builder.stars(stargazers);
        builder.lastUpdate(pushedAt.toInstant().atZone(ZoneId.of("UTC")).toLocalDateTime());
        builder.lastCommitSHA(lastCommitSHA);
        return builder;
    }
}
