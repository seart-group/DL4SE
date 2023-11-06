package ch.usi.si.seart.crawler.scheduling;

import ch.usi.si.seart.analyzer.Analyzer;
import ch.usi.si.seart.analyzer.AnalyzerCustomizer;
import ch.usi.si.seart.analyzer.LocalClone;
import ch.usi.si.seart.crawler.dto.SearchResultDto;
import ch.usi.si.seart.crawler.git.Git;
import ch.usi.si.seart.crawler.git.GitException;
import ch.usi.si.seart.crawler.http.HttpClient;
import ch.usi.si.seart.crawler.io.ExtensionBasedFileVisitor;
import ch.usi.si.seart.crawler.service.FileService;
import ch.usi.si.seart.crawler.service.FileSystemService;
import ch.usi.si.seart.exception.EntityNotFoundException;
import ch.usi.si.seart.model.GitRepo;
import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.model.code.File;
import ch.usi.si.seart.model.code.Function;
import ch.usi.si.seart.model.job.Job;
import ch.usi.si.seart.service.CrawlJobService;
import ch.usi.si.seart.service.GitRepoService;
import ch.usi.si.seart.service.LanguageService;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.common.base.Strings;
import com.google.common.collect.Sets;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CodeCrawler implements Runnable {

    HttpClient httpClient;

    FileService fileService;
    GitRepoService gitRepoService;
    CrawlJobService crawlJobService;
    LanguageService languageService;
    ConversionService conversionService;
    FileSystemService fileSystemService;

    Predicate<String> nameFilter;

    Predicate<Path> fileFilter;

    AnalyzerCustomizer<Analyzer> analyzerCustomizer;

    ObjectFactory<GenericUrl> baseUrlFactory;

    Set<String> languageNames = new HashSet<>();
    Map<String, Language> nameToLanguage = new HashMap<>();

    @PostConstruct
    private void postConstruct() {
        languageService.getAll().forEach(language -> {
            String name = language.getName();
            List<String> extensions = language.getExtensions();
            languageNames.add(name);
            nameToLanguage.put(name, language);
        });
    }

    public void run() {
        GenericUrl url = baseUrlFactory.getObject();
        LocalDate checkpoint = crawlJobService.getProgress(Job.CODE)
                .getCheckpoint()
                .toLocalDate();
        log.info("Last mining checkpoint: {}", checkpoint);
        do url = fetchSearchResults(url, checkpoint);
        while (url != null);
    }

    @SneakyThrows(IOException.class)
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
        crawlJobService.saveProgress(Job.CODE, checkpoint);
    }

    private void inspectSearchResult(SearchResultDto item) {
        String name = item.getName();

        if (nameFilter.test(name)) {
            log.debug("Skipping: {}. Repository is set to be ignored by crawler!", name);
            return;
        }

        LocalDateTime lastUpdateGhs = conversionService.convert(item.getLastCommit(), LocalDateTime.class);

        Set<String> repoLanguageNames = Sets.intersection(languageNames, item.getAllLanguages());
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

    private void updateRepoData(GitRepo repo, Set<Language> repoLanguages) {
        String name = repo.getName();
        LocalDateTime lastCommit = repo.getLastCommit();
        log.info("Updating repository: {} [Last Commit: {}]", name, lastCommit);
        Path localDirectory = fileSystemService.createTemporaryDirectory();
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
            diff.getAdded().stream()
                    .filter(fileFilter)
                    .forEach(path -> addFile(localClone, path));
            diff.getDeleted().forEach(path -> deleteFile(repo, path));
            diff.getModified().forEach(path -> {
                deleteFile(repo, path);
                if (fileFilter.test(path))
                    addFile(localClone, path);
            });
            diff.getRenamed().forEach((key, value) -> {
                boolean validOld = fileFilter.test(key);
                boolean validNew = fileFilter.test(value);
                if (validOld && validNew)
                    renameFile(repo, key, value);
                else if (validOld)
                    deleteFile(repo, key);
                else if (validNew)
                    addFile(localClone, value);
            });
            diff.getEdited().forEach((key, value) -> {
                deleteFile(repo, key);
                if (fileFilter.test(value))
                    addFile(localClone, value);
            });
            diff.getCopied()
                    .values()
                    .stream()
                    .filter(fileFilter)
                    .forEach(value -> addFile(localClone, value));

            gitRepoService.createOrUpdate(repo);
        } catch (GitException ex) {
            log.error("Git operation error for: " + name, ex);
        } catch (IOException ex) {
            log.error("Error occurred during cleanup of: " + name, ex.getCause());
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

    private void mineRepoData(GitRepo repo, Set<Language> languages) {
        String name = repo.getName();
        LocalDateTime lastUpdateGhs = repo.getLastCommit();
        log.info("Mining repository: {} [Last Commit: {}]", name, lastUpdateGhs);
        Path localDirectory = fileSystemService.createTemporaryDirectory();
        LocalClone localClone = new LocalClone(repo, localDirectory);
        try (Git git = new Git(name, localDirectory, true)) {
            Git.Commit latest = git.getLastCommitInfo();
            mineRepoData(localClone, languages);
            repo.setLanguages(languages);
            repo.setLastCommit(latest.getTimestamp());
            repo.setLastCommitSHA(latest.getSha());
            gitRepoService.createOrUpdate(repo);
        } catch (GitException ex) {
            log.error("Git operation error for: " + name, ex);
        } catch (IOException ex) {
            log.error("Error occurred during cleanup of: " + name, ex.getCause());
        }
    }

    private void mineRepoData(LocalClone localClone, Set<Language> languages) {
        GitRepo repo = localClone.getGitRepo();
        Path localDirectory = localClone.getDiskPath();
        String[] extensions = languages.stream()
                .map(Language::getExtensions)
                .flatMap(Collection::stream)
                .toArray(String[]::new);
        ExtensionBasedFileVisitor visitor = ExtensionBasedFileVisitor.forExtensions(extensions);
        try {
            Files.walkFileTree(localDirectory, visitor);
            Set<Path> candidates = new HashSet<>(visitor.getVisited());
            Set<Path> analyzed = fileService.getAllPathsByRepo(repo).stream()
                    .map(localDirectory::resolve)
                    .collect(Collectors.toSet());
            Set<Path> filtered = Sets.difference(candidates, analyzed).stream()
                    .filter(fileFilter)
                    .collect(Collectors.toSet());
            filtered.forEach(target -> analyzeAndStore(localClone, target));
        } catch (IOException ex) {
            log.error("Could not walk file tree for: " + repo.getName(), ex);
        }
    }

    private void analyzeAndStore(LocalClone localClone, Path path) {
        Path relative = localClone.relativePathOf(path);
        log.debug("Analyzing file: {}", relative);
        Language language = languageService.getAssociatedWith(path);
        try (Analyzer analyzer = new Analyzer(localClone, path)) {
            analyzerCustomizer.customize(analyzer);
            Analyzer.Result result = analyzer.analyze();
            File file = result.getFile();
            List<Function> functions = result.getFunctions();
            file.setLanguage(language);
            functions.forEach(function -> function.setLanguage(language));
            fileService.create(file);
        } catch (Exception ex) {
            log.error("Exception occurred while analyzing: {}", relative, ex);
        }
    }
}
