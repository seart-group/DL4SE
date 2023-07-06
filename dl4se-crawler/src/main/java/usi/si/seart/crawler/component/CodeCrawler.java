package usi.si.seart.crawler.component;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.convert.ConversionService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import usi.si.seart.analyzer.Analyzer;
import usi.si.seart.analyzer.AnalyzerFactory;
import usi.si.seart.analyzer.LocalClone;
import usi.si.seart.crawler.dto.SearchResultDto;
import usi.si.seart.crawler.git.Git;
import usi.si.seart.crawler.git.GitException;
import usi.si.seart.crawler.io.ExtensionBasedFileVisitor;
import usi.si.seart.crawler.service.CrawlJobService;
import usi.si.seart.crawler.service.FileService;
import usi.si.seart.crawler.service.GitRepoService;
import usi.si.seart.crawler.service.LanguageService;
import usi.si.seart.model.GitRepo;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;
import usi.si.seart.model.code.Function;

import javax.annotation.PostConstruct;
import javax.persistence.EntityNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
@Component
@ConditionalOnProperty(value = "app.crawl-job.type", havingValue = "CODE")
//@ConditionalOnExpression("#{jobType == T(usi.si.seart.model.job.Job).CODE}")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodeCrawler implements Runnable {

    Duration nextRunDelay;
    HttpClient httpClient;
    GenericUrl baseUrl;

    FileService fileService;
    GitRepoService gitRepoService;
    CrawlJobService crawlJobService;
    LanguageService languageService;
    ConversionService conversionService;

    @NonFinal
    @Value("${app.general.tmp-dir-prefix}")
    String prefix;

    Set<String> languageNames = new HashSet<>();
    Map<String, Language> nameToLanguage = new HashMap<>();
    Map<String, Language> extensionToLanguage = new HashMap<>();

    @PostConstruct
    private void postConstruct() {
        languageService.getAll().forEach(language -> {
            String name = language.getName();
            List<String> extensions = language.getExtensions();
            languageNames.add(name);
            nameToLanguage.put(name, language);
            extensions.forEach(extension -> extensionToLanguage.put(extension, language));
        });
    }

    @Scheduled(fixedDelayString = "${app.crawl-job.next-run-delay}")
    public void run() {
        GenericUrl url = baseUrl;
        LocalDate checkpoint = crawlJobService.getProgress()
                .getCheckpoint()
                .toLocalDate();
        log.info("Last mining checkpoint: {}", checkpoint);
        do {
            url = fetchSearchResults(url, checkpoint);
        } while (url != null);
        LocalDateTime nextRun = LocalDateTime.now()
                .plus(nextRunDelay)
                .truncatedTo(ChronoUnit.SECONDS);
        log.info("Finished! Next run scheduled for: {}", nextRun);
    }

    @SneakyThrows
    private GenericUrl fetchSearchResults(GenericUrl url, LocalDate checkpoint) {
        GenericUrl requestUrl = url.set("committedMin", checkpoint.toString())
                                    .set("sort", "lastCommit")
                                    .set("size", 100);
        HttpResponse response = httpClient.getRequest(requestUrl);
        List<SearchResultDto> items = httpClient.getSearchResults(response);
        Map<String, String> links = httpClient.getNavigationLinks(response);
        items.forEach(this::inspectSearchResult);
        response.disconnect();
        Optional<String> optional = Optional.ofNullable(links.get("next"));
        return optional.map(GenericUrl::new).orElse(null);
    }

    private void saveCrawlProgress(LocalDateTime checkpoint) {
        if (checkpoint == null || checkpoint.isAfter(LocalDateTime.now())) return;
        log.debug("Saving progress... [Checkpoint: {}]", checkpoint);
        crawlJobService.saveProgress(checkpoint);
    }

    private void inspectSearchResult(SearchResultDto item) {
        String name = item.getName();
        LocalDateTime lastUpdateGhs = conversionService.convert(item.getLastCommit(), LocalDateTime.class);

        Set<String> repoLanguageNames = Sets.intersection(languageNames, item.getRepoLanguages());
        Set<Language> repoLanguages = repoLanguageNames.stream()
                .map(nameToLanguage::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        if (repoLanguages.isEmpty()) {
            log.debug("Skipping: {}. No files of interest found!", name);
            return;
        }

        GitRepo repo;
        BiConsumer<GitRepo, Set<Language>> operation;
        try {
            repo = gitRepoService.getByName(name);
            item.update(repo);
            operation = this::updateRepoData;
        } catch (EntityNotFoundException ignored) {
            repo = conversionService.convert(item, GitRepo.class);
            repo = gitRepoService.createOrUpdate(repo);
            operation = this::mineRepoData;
        }

        operation.accept(repo, repoLanguages);
        saveCrawlProgress(lastUpdateGhs);
    }

    @SneakyThrows(IOException.class)
    private void updateRepoData(GitRepo repo, Set<Language> repoLanguages) {
        String name = repo.getName();
        LocalDateTime lastCommit = repo.getLastCommit();

        log.info("Updating repository: {} [Last Commit: {}]", name, lastCommit);
        Path localDirectory = Files.createTempDirectory(prefix);
        LocalClone localClone = new LocalClone(repo, localDirectory);
        try (Git git = new Git(name, localDirectory, lastCommit)) {

            Set<Language> notMined = Sets.difference(repoLanguages, repo.getLanguages());
            if (!notMined.isEmpty()) {
                mineRepoData(localClone, notMined);
                repo.setLanguages(repoLanguages);
                gitRepoService.createOrUpdate(repo);
            }

            Git.Commit latest = git.getLastCommitInfo();
            repo.setLastCommit(latest.getTimestamp());
            repo.setLastCommitSHA(latest.getSha());

            Git.Diff diff = git.getDiff(repo.getLastCommitSHA(), repo.getLanguages());
            if (!Strings.isNullOrEmpty(diff.toString()))
                log.debug("Diff since last update:\n{}", diff);
            diff.getAdded().forEach(path -> addFile(localClone, path));
            diff.getDeleted().forEach(path -> deleteFile(repo, path));
            diff.getModified().forEach(path -> {
                deleteFile(repo, path);
                addFile(localClone, path);
            });
            diff.getRenamed().forEach((key, value) -> renameFile(repo, key, value));
            diff.getEdited().forEach((key, value) -> {
                deleteFile(repo, key);
                addFile(localClone, value);
            });
            diff.getCopied().forEach((key, value) -> addFile(localClone, value));

            gitRepoService.createOrUpdate(repo);
        } catch (GitException ex) {
            log.error("Git operation error for: " + name, ex);
        }
    }
    
    private void addFile(LocalClone localClone, Path path) {
        analyzeAndStore(localClone, path);
    }

    private void deleteFile(GitRepo repo, Path path) {
        fileService.delete(repo, path);
    }

    private void renameFile(GitRepo repo, Path oldPath, Path newPath) {
        fileService.rename(repo, oldPath, newPath);
    }

    @SneakyThrows(IOException.class)
    private void mineRepoData(GitRepo repo, Set<Language> languages) {
        String name = repo.getName();
        LocalDateTime lastUpdateGhs = repo.getLastCommit();

        Path localDirectory = Files.createTempDirectory(prefix);
        LocalClone localClone = new LocalClone(repo, localDirectory);
        log.info("Mining repository: {} [Last Commit: {}]", name, lastUpdateGhs);
        try (Git git = new Git(name, localDirectory, true)) {
            Git.Commit latest = git.getLastCommitInfo();

            mineRepoData(localClone, languages);

            repo.setLanguages(languages);
            repo.setLastCommit(latest.getTimestamp());
            repo.setLastCommitSHA(latest.getSha());

            gitRepoService.createOrUpdate(repo);
        } catch (GitException ex) {
            log.error("Git operation error for: " + name, ex);
        }
    }

    @SneakyThrows(IOException.class)
    private void mineRepoData(LocalClone localClone, Set<Language> languages) {
        GitRepo repo = localClone.getGitRepo();
        Path localDirectory = localClone.getDiskPath();
        String[] extensions = languages.stream()
                .map(Language::getExtensions)
                .flatMap(Collection::stream)
                .toArray(String[]::new);
        ExtensionBasedFileVisitor visitor = ExtensionBasedFileVisitor.forExtensions(extensions);
        Files.walkFileTree(localDirectory, visitor);
        Set<Path> candidates = new HashSet<>(visitor.getVisited());
        Set<Path> analyzed = fileService.getAllByRepo(repo).stream()
                .map(File::getPath)
                .map(Path::of)
                .map(localDirectory::resolve)
                .collect(Collectors.toSet());
        Set<Path> targets = Sets.difference(candidates, analyzed);
        targets.forEach(target -> analyzeAndStore(localClone, target));
    }

    private void analyzeAndStore(LocalClone localClone, Path path) {
        log.trace("Analyzing file: {}", localClone.relativePathOf(path));
        String extension = com.google.common.io.Files.getFileExtension(path.toString());
        Language language = extensionToLanguage.get(extension);
        try (Analyzer analyzer = AnalyzerFactory.getAnalyzer(language).apply(localClone, path)) {
            Analyzer.Result result = analyzer.analyze();
            File file = result.getFile();
            List<Function> functions = result.getFunctions();
            file.setLanguage(language);
            functions.forEach(function -> function.setLanguage(language));
            fileService.create(file);
        } catch (Exception ex) {
            log.error("", ex);
        }
    }
}
