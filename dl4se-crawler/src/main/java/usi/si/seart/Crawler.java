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
import usi.si.seart.git.Git;
import usi.si.seart.git.GitException;
import usi.si.seart.model.job.CrawlJob;
import usi.si.seart.parser.ParsingException;
import usi.si.seart.http.HttpClient;
import usi.si.seart.http.payload.GhsGitRepo;
import usi.si.seart.io.ExtensionBasedFileVisitor;
import usi.si.seart.model.GitRepo;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;
import usi.si.seart.parser.FallbackParser;
import usi.si.seart.parser.Parser;
import usi.si.seart.utils.HibernateUtils;
import usi.si.seart.utils.PathUtils;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Crawler {

    static CrawlJob lastJob;

    static Set<Language> languages;
    static Set<String> names;
    static String[] extensions;
    static Map<String, Language> nameToLanguage;
    static Map<String, Language> extensionToLanguage;

    static {
        lastJob = HibernateUtils.getLastJob();

        languages = HibernateUtils.getLanguages();
        extensionToLanguage = languages.stream().flatMap(language -> {
            List<Tuple<String, Language>> entries = new ArrayList<>();
            for (String extension: language.getExtensions()) {
                entries.add(Tuple.of(extension, language));
            }
            return entries.stream();
        }).collect(Collectors.toMap(Tuple::getKey, Tuple::getValue));
        extensions = extensionToLanguage.keySet().toArray(new String[0]);
        nameToLanguage = languages.stream().map(language -> Tuple.of(language.getName(), language))
                .collect(Collectors.toMap(Tuple::getKey, Tuple::getValue));
        names = nameToLanguage.keySet();
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
                .set("sort", "pushedAt");
        HttpResponse response = client.getRequest(requestUrl);
        List<GhsGitRepo> items = client.getSearchResults(response);
        Map<String, String> links = client.getNavigationLinks(response);
        items.forEach(Crawler::mineRepositoryData);
        response.disconnect();
        return links.get("next");
    }

    @SneakyThrows
    private static void mineRepositoryData(GhsGitRepo item) {
        String name = item.getName();

        Set<String> ghsLanguages = item.getRepoLanguages();
        Set<String> supported = CollectionUtils.intersection(names, ghsLanguages);

        if (supported.isEmpty()) {
            log.debug("Skipping: {}. No files of interest found!", name);
            return;
        }

        GitRepo.GitRepoBuilder repoBuilder = item.toGitRepoBuilder();

        Path cloneDir = Files.createTempDirectory(CrawlerProperties.tmpDirPrefix);
        log.info("Mining repository: {} [Last Update: {}]", name, item.getPushedAt());
        try {
            Git git = new Git(name, cloneDir, true);
            Git.Commit latest = git.getLastCommitInfo();
            repoBuilder.lastCommitSHA(latest.getSha());
            repoBuilder.lastUpdate(latest.getLastUpdate());

            ExtensionBasedFileVisitor visitor = new ExtensionBasedFileVisitor(extensions);
            Files.walkFileTree(cloneDir, visitor);
            List<Path> paths = visitor.getVisited();
            paths.forEach(path -> parseFile(cloneDir, path, repoBuilder));
            GitRepo repo = repoBuilder.build();
            repo.getFiles().forEach(file -> file.setRepo(repo));
            repo.getFunctions().forEach(function -> function.setRepo(repo));
            HibernateUtils.save(repo);

            LocalDateTime ghsTimestamp = DateToLDTConverter.getInstance().convert(item.getPushedAt());
            lastJob.setCheckpoint(ghsTimestamp);
            HibernateUtils.save(lastJob);
        } catch (GitException ex) {
            log.error("Git operation error for: {}", name);
            log.error("Error stack trace:", ex);
        } finally {
            PathUtils.forceDelete(cloneDir);
        }
    }

    private static void parseFile(Path cloneDir, Path filePath, GitRepo.GitRepoBuilder repoBuilder) {
        String extension = PathUtils.getExtension(filePath);
        Language language = extensionToLanguage.get(extension);

        Parser parser = Parser.getParser(language);
        File file;
        try {
            file = parser.parse(filePath);
        } catch (ParsingException ignored) {
            parser = new FallbackParser(language);
            file = parser.parse(filePath);
        }

        if (file == null) return;
        file.setPath(FileSystems.getDefault().getSeparator() + cloneDir.relativize(filePath));
        repoBuilder.file(file);
        repoBuilder.functions(file.getFunctions());
    }
}
