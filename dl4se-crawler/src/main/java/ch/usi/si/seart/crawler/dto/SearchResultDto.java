package ch.usi.si.seart.crawler.dto;

import ch.usi.si.seart.model.GitRepo;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class SearchResultDto {

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
    Long blankLines;
    Long codeLines;
    Long commentLines;
    List<MetricDto> metrics;
    Date lastCommit;
    String lastCommitSHA;
    Boolean hasWiki;
    Boolean isArchived;
    Map<String, Long> languages;
    Set<String> labels;
    Set<String> topics;

    public Set<String> getAllLanguages() {
        return Stream.concat(
                languages.keySet().stream(),
                Stream.of(mainLanguage)
        ).collect(Collectors.toSet());
    }

    public void update(GitRepo repo) {
        repo.setLicense(license);
        if (commits != null) repo.setCommits(commits);
        if (contributors != null) repo.setContributors(contributors);
        if (totalIssues != null) repo.setIssues(totalIssues);
        if (stargazers != null) repo.setStars(stargazers);
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class MetricDto {
        String language;
        Long blankLines;
        Long codeLines;
        Long commentLines;
    }
}
