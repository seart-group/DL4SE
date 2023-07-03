package usi.si.seart;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import usi.si.seart.collection.Tuple;
import usi.si.seart.collection.utils.CollectionUtils;
import usi.si.seart.converter.DateToLDTConverter;
import usi.si.seart.converter.GhsToGitRepoConverter;
import usi.si.seart.git.Git;
import usi.si.seart.git.GitException;
import usi.si.seart.http.HttpClient;
import usi.si.seart.http.payload.GhsGitRepo;
import usi.si.seart.io.ExtensionBasedFileVisitor;
import usi.si.seart.model.GitRepo;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;
import usi.si.seart.model.job.CrawlJob;
import usi.si.seart.parser.FallbackParser;
import usi.si.seart.parser.Parser;
import usi.si.seart.parser.ParsingException;
import usi.si.seart.utils.HibernateUtils;
import usi.si.seart.utils.PathUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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
        LocalDateTime lastUpdateGhs = DateToLDTConverter.getInstance().convert(item.getLastCommit());
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
            repo = GhsToGitRepoConverter.getInstance().convert(item);
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
        Path cloneDir = Files.createTempDirectory(CrawlerProperties.tmpDirPrefix);
        try (Git git = new Git(name, cloneDir, lastCommit)) {

            Set<Language> notMined = CollectionUtils.difference(repoLanguages, repo.getLanguages());
            if (!notMined.isEmpty()) {
                mineRepoDataForLanguages(repo, cloneDir, notMined);
                repo.setLanguages(repoLanguages);
                HibernateUtils.save(repo);
            }

            Git.Commit latest = git.getLastCommitInfo();
            repo.setLastCommit(latest.getTimestamp());
            repo.setLastCommitSHA(latest.getSha());

            Git.Diff diff = git.getDiff(repo.getLastCommitSHA(), repo.getLanguages());
            diff.getAdded().forEach(path -> addFile(repo, path, cloneDir));
            diff.getDeleted().forEach(path -> deleteFile(repo, path));
            diff.getModified().forEach(path -> {
                deleteFile(repo, path);
                addFile(repo, path, cloneDir);
            });
            diff.getRenamed().forEach((key, value) -> renameFile(repo, key, value));
            diff.getEdited().forEach((key, value) -> {
                deleteFile(repo, key);
                addFile(repo, value, cloneDir);
            });
            diff.getCopied().forEach((key, value) -> addFile(repo, value, cloneDir));

            HibernateUtils.save(repo);
        } catch (GitException ex) {
            log.error("Git operation error for: " + name, ex);
        }
    }

    private static void addFile(GitRepo repo, Path filePath, Path dirPath) {
        Path absolute = dirPath.resolve(filePath);
        File file = parseFile(absolute);
        if (file != null) {
            file.setPath(filePath.toString());
            file.setRepo(repo);
            file.getFunctions().forEach(function -> function.setRepo(repo));
            HibernateUtils.save(file);
        }
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

        Path cloneDir = Files.createTempDirectory(CrawlerProperties.tmpDirPrefix);
        log.info("Mining repository: {} [Last Commit: {}]", name, lastUpdateGhs);
        try (Git git = new Git(name, cloneDir, true)) {
            Git.Commit latest = git.getLastCommitInfo();

            mineRepoDataForLanguages(repo, cloneDir, repoLanguages);

            repo.setLanguages(repoLanguages);
            repo.setLastCommit(latest.getTimestamp());
            repo.setLastCommitSHA(latest.getSha());

            HibernateUtils.save(repo);
        } catch (GitException ex) {
            log.error("Git operation error for: " + name, ex);
        }
    }

    @SneakyThrows
    private static void mineRepoDataForLanguages(GitRepo repo, Path dirPath, Set<Language> languages) {
        String[] extensions = languages.stream()
                .map(Language::getExtensions)
                .flatMap(Collection::stream)
                .toArray(String[]::new);
        ExtensionBasedFileVisitor visitor = ExtensionBasedFileVisitor.forExtensions(extensions);
        Files.walkFileTree(dirPath, visitor);
        List<Path> paths = visitor.getVisited();
        for (Path path: paths) {
            File file = parseFile(path);
            if (file != null) {
                file.setPath(dirPath.relativize(path).toString());
                file.setRepo(repo);
                file.getFunctions().forEach(function -> function.setRepo(repo));
                HibernateUtils.save(file);
            }
        }
    }

    private static File parseFile(Path filePath) {
        String extension = com.google.common.io.Files.getFileExtension(filePath.toString());
        Language language = extensionToLanguage.get(extension);

        Parser parser = Parser.getParser(language);
        File file;
        try {
            file = parser.parse(filePath);
        } catch (ParsingException ignored) {
            parser = new FallbackParser(language);
            file = parser.parse(filePath);
        }

        return file;
    }
}
