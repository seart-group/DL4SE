package usi.si.seart;

import ch.usi.si.seart.treesitter.LibraryLoader;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import com.google.common.base.Strings;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import usi.si.seart.analyzer.Analyzer;
import usi.si.seart.analyzer.AnalyzerFactory;
import usi.si.seart.analyzer.LocalClone;
import usi.si.seart.collection.Tuple;
import usi.si.seart.collection.utils.CollectionUtils;
import usi.si.seart.converter.DateToLocalDateTime;
import usi.si.seart.converter.GhsGitRepoToGitRepo;
import usi.si.seart.git.Git;
import usi.si.seart.git.GitException;
import usi.si.seart.http.HttpClient;
import usi.si.seart.http.payload.GhsGitRepo;
import usi.si.seart.io.ExtensionBasedFileVisitor;
import usi.si.seart.model.GitRepo;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;
import usi.si.seart.model.job.CrawlJob;
import usi.si.seart.utils.HibernateUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Crawler {

    static CrawlJob lastJob;

    static Set<Language> languages;

    static Set<String> names;
    static Map<String, Language> nameToLanguage;

    static String[] extensions;
    static Map<String, Language> extensionToLanguage;

    static {
        LibraryLoader.load();

        lastJob = HibernateUtils.getLastJob();

        languages = HibernateUtils.getLanguages();

        nameToLanguage = languages.stream().map(language -> Tuple.of(language.getName(), language))
                .collect(Collectors.toMap(Tuple::getKey, Tuple::getValue));
        names = nameToLanguage.keySet();

        extensionToLanguage = languages.stream().flatMap(language -> {
            List<Tuple<String, Language>> entries = new ArrayList<>();
            for (String extension: language.getExtensions()) {
                entries.add(Tuple.of(extension, language));
            }
            return entries.stream();
        }).collect(Collectors.toMap(Tuple::getKey, Tuple::getValue));
        extensions = extensionToLanguage.keySet().toArray(String[]::new);
    }

    public static void main(String[] args) {
        HttpClient client = new HttpClient();
        String nextUrl = CrawlerProperties.ghsSearchUrl;
        LocalDate lastUpdate = lastJob.getCheckpoint().toLocalDate();
        log.info("Last mining checkpoint: {}", lastUpdate);
        do {
            nextUrl = iterate(client, nextUrl, lastUpdate);
        } while (nextUrl != null);
        log.info("Done!");
    }

    @SneakyThrows
    private static String iterate(HttpClient client, String link, LocalDate lastUpdate) {
        GenericUrl requestUrl = new GenericUrl(link)
                .set("committedMin", lastUpdate.toString())
                .set("sort", "lastCommit")
                .set("size", 100);
        HttpResponse response = client.getRequest(requestUrl);
        List<GhsGitRepo> items = client.getSearchResults(response);
        Map<String, String> links = client.getNavigationLinks(response);
        items.forEach(Crawler::checkRepoData);
        response.disconnect();
        return links.get("next");
    }

    // FIXME 06.05.22: This is only a temporary workaround until I don't figure out why the validation does not work
    private static void saveProgress() {
        Long id = lastJob.getId();
        LocalDateTime checkpoint = lastJob.getCheckpoint();
        if (checkpoint.isAfter(LocalDateTime.now())) return;
        log.debug("Saving progress... [Checkpoint: {}]", checkpoint);
        HibernateUtils.updateCrawlJobById(id, checkpoint);
    }

    private static void checkRepoData(GhsGitRepo item) {
        String name = item.getName();
        LocalDateTime lastUpdateGhs = DateToLocalDateTime.getConverter().convert(item.getLastCommit());
        lastJob.setCheckpoint(lastUpdateGhs);

        Set<String> repoLanguageNames = CollectionUtils.intersection(names, item.getRepoLanguages());
        Set<Language> repoLanguages = CollectionUtils.getAllValuesFrom(nameToLanguage, repoLanguageNames);

        if (repoLanguages.isEmpty()) {
            log.debug("Skipping: {}. No files of interest found!", name);
            return;
        }

        Optional<GitRepo> optional = HibernateUtils.getRepoByName(name);
        GitRepo repo;
        BiConsumer<GitRepo, Set<Language>> operation;
        if (optional.isPresent()) {
            repo = optional.get();
            item.update(repo);
            operation = Crawler::updateRepoData;
        } else {
            repo = GhsGitRepoToGitRepo.getConverter().convert(item);
            HibernateUtils.save(repo);
            operation = Crawler::mineRepoData;
        }

        operation.accept(repo, repoLanguages);
        saveProgress();
    }

    @SneakyThrows(IOException.class)
    private static void updateRepoData(GitRepo repo, Set<Language> repoLanguages) {
        String name = repo.getName();
        LocalDateTime lastCommit = repo.getLastCommit();

        log.info("Updating repository: {} [Last Commit: {}]", name, lastCommit);
        Path localDirectory = Files.createTempDirectory(CrawlerProperties.tmpDirPrefix);
        LocalClone localClone = new LocalClone(repo, localDirectory);
        try (Git git = new Git(name, localDirectory, lastCommit)) {

            Set<Language> notMined = CollectionUtils.difference(repoLanguages, repo.getLanguages());
            if (!notMined.isEmpty()) {
                mineRepoDataForLanguages(localClone, notMined);
                repo.setLanguages(repoLanguages);
                HibernateUtils.save(repo);
            }

            Git.Commit latest = git.getLastCommitInfo();
            repo.setLastCommit(latest.getTimestamp());
            repo.setLastCommitSHA(latest.getSha());

            Git.Diff diff = git.getDiff(repo.getLastCommitSHA(), repo.getLanguages());
            if (!Strings.isNullOrEmpty(diff.toString()))
                log.debug("Diff since last update:\n{}", diff);
            diff.getAdded().forEach(path -> addFile(repo, path, localDirectory));
            diff.getDeleted().forEach(path -> deleteFile(repo, path));
            diff.getModified().forEach(path -> {
                deleteFile(repo, path);
                addFile(repo, path, localDirectory);
            });
            diff.getRenamed().forEach((key, value) -> renameFile(repo, key, value));
            diff.getEdited().forEach((key, value) -> {
                deleteFile(repo, key);
                addFile(repo, value, localDirectory);
            });
            diff.getCopied().forEach((key, value) -> addFile(repo, value, localDirectory));

            HibernateUtils.save(repo);
        } catch (GitException ex) {
            log.error("Git operation error for: " + name, ex);
        }
    }

    private static void addFile(GitRepo repo, Path filePath, Path dirPath) {
        LocalClone localClone = new LocalClone(repo, dirPath);
        tryAnalyze(localClone, filePath)
                .map(Analyzer.Result::getFile)
                .ifPresent(HibernateUtils::save);
    }

    private static void deleteFile(GitRepo repo, Path filePath) {
        HibernateUtils.deleteFileByRepoIdAndPath(repo.getId(), filePath);
    }

    private static void renameFile(GitRepo repo, Path oldFilePath, Path newFilePath) {
        HibernateUtils.updateFilePathByRepoId(repo.getId(), oldFilePath, newFilePath);
    }

    @SneakyThrows(IOException.class)
    private static void mineRepoData(GitRepo repo, Set<Language> repoLanguages) {
        String name = repo.getName();
        LocalDateTime lastUpdateGhs = repo.getLastCommit();

        Path localDirectory = Files.createTempDirectory(CrawlerProperties.tmpDirPrefix);
        LocalClone localClone = new LocalClone(repo, localDirectory);
        log.info("Mining repository: {} [Last Commit: {}]", name, lastUpdateGhs);
        try (Git git = new Git(name, localDirectory, true)) {
            Git.Commit latest = git.getLastCommitInfo();

            mineRepoDataForLanguages(localClone, repoLanguages);

            repo.setLanguages(repoLanguages);
            repo.setLastCommit(latest.getTimestamp());
            repo.setLastCommitSHA(latest.getSha());

            HibernateUtils.save(repo);
        } catch (GitException ex) {
            log.error("Git operation error for: " + name, ex);
        }
    }

    @SneakyThrows(IOException.class)
    private static void mineRepoDataForLanguages(LocalClone localClone, Set<Language> languages) {
        GitRepo repo = localClone.getGitRepo();
        Path localDirectory = localClone.getDiskPath();
        String[] extensions = languages.stream()
                .map(Language::getExtensions)
                .flatMap(Collection::stream)
                .toArray(String[]::new);
        ExtensionBasedFileVisitor visitor = ExtensionBasedFileVisitor.forExtensions(extensions);
        Files.walkFileTree(localDirectory, visitor);
        Set<Path> candidates = new HashSet<>(visitor.getVisited());
        Set<Path> analyzed = HibernateUtils.getFilesByRepo(repo.getId()).stream()
                .map(File::getPath)
                .map(Path::of)
                .map(localDirectory::resolve)
                .collect(Collectors.toSet());
        Set<Path> targets = CollectionUtils.difference(candidates, analyzed);
        for (Path path: targets) {
            tryAnalyze(localClone, path)
                    .map(Analyzer.Result::getFile)
                    .ifPresent(HibernateUtils::save);
        }
    }

    private static Optional<Analyzer.Result> tryAnalyze(LocalClone localClone, Path filePath) {
        log.trace("Analyzing file: {}", localClone.relativePathOf(filePath));
        String extension = com.google.common.io.Files.getFileExtension(filePath.toString());
        Language language = extensionToLanguage.get(extension);
        try (Analyzer analyzer = AnalyzerFactory.getAnalyzer(language).apply(localClone, filePath)) {
            Analyzer.Result result = analyzer.analyze();
            result.getFile().setLanguage(language);
            result.getFunctions().forEach(function -> function.setLanguage(language));
            return Optional.of(result);
        } catch (Exception ex) {
            log.error("", ex);
            return Optional.empty();
        }
    }
}
