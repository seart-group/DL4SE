package usi.si.seart;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpResponse;
import lombok.AccessLevel;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import usi.si.seart.collection.utils.SetUtils;
import usi.si.seart.exception.ParsingException;
import usi.si.seart.http.HttpClient;
import usi.si.seart.http.payload.GhsGitRepo;
import usi.si.seart.io.ExtensionBasedFileVisitor;
import usi.si.seart.model.GitRepo;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;
import usi.si.seart.parser.FallbackParser;
import usi.si.seart.parser.Parser;
import usi.si.seart.utils.GitUtils;
import usi.si.seart.utils.HibernateUtils;
import usi.si.seart.utils.PathUtils;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class Crawler {

    // TODO: 17.02.22 Parametrize these in the settings
    static String tempPrefix = "dl4se";
    static String startUrl = "http://localhost:8080/api/r/search";
    static LocalDate startDate = LocalDate.of(2008, 1, 1);

    static Set<Language> languages;

    static {
        try (Session session = HibernateUtils.getFactory().openSession()) {
            languages = session.createQuery("SELECT l FROM Language l", Language.class)
                    .stream()
                    .collect(Collectors.toSet());
        } catch (HibernateException ex) {
            log.error("Could not open session for initialization:", ex);
            log.error("Aborting...");
            System.exit(1);
        }
    }

    static Map<String, Language> extensionToLanguage = languages.stream().flatMap(language -> {
        List<Map.Entry<String, Language>> entries = new ArrayList<>();
        for (String extension : language.getExtensions()) {
            entries.add(Map.entry(extension, language));
        }
        return entries.stream();
    }).collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    static String[] extensions = extensionToLanguage.keySet().toArray(new String[0]);

    static Set<String> languageNames = languages.stream()
            .map(Language::getName)
            .collect(Collectors.toSet());

    public static void main(String[] args) {
        HttpClient client = new HttpClient();
        String nextUrl = startUrl;
        // The default starting date, will be overridden with job value stored in the DB
        LocalDate lastUpdate = startDate;
        do {
            nextUrl = iterate(client, nextUrl, lastUpdate);
        } while (nextUrl != null);
        log.info("Done!");
    }

    @SneakyThrows
    private static String iterate(HttpClient client, String link, LocalDate lastUpdate) {
        GenericUrl requestUrl = new GenericUrl(link)
                .set("pushedAtMin", lastUpdate.toString())
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
        boolean skip = languageNames.contains(item.getMainLanguage());
        skip |= SetUtils.intersection(languageNames, item.getLanguages().keySet()).isEmpty();
        if (skip) {
            log.debug("Skipping: {}. No files of interest found!", name);
            return;
        }

        GitRepo.GitRepoBuilder repoBuilder = item.toGitRepoBuilder();

        Path cloneDir = Files.createTempDirectory(tempPrefix);
        log.info("Mining repository: {} [Last Update: {}]", name, item.getPushedAt());
        try {
            Git git = GitUtils.cloneRepository(name, cloneDir);
            for (RevCommit latest: git.log().setMaxCount(1).call()) {
                repoBuilder.lastCommitSHA(latest.getName());
                repoBuilder.lastUpdate(
                        LocalDateTime.ofInstant(Instant.ofEpochSecond(latest.getCommitTime()), ZoneId.of("UTC"))
                );
            }

            ExtensionBasedFileVisitor visitor = new ExtensionBasedFileVisitor(extensions);
            Files.walkFileTree(cloneDir, visitor);
            List<Path> paths = visitor.getVisited();
            paths.forEach(path -> parseFile(cloneDir, path, repoBuilder));
            GitRepo repo = repoBuilder.build();
            repo.getFiles().forEach(file -> file.setRepo(repo));
            repo.getFunctions().forEach(function -> function.setRepo(repo));
            HibernateUtils.saveRepo(repo);
        } catch (GitAPIException ex) {
            log.error("Error while cloning: {}", name);
            log.error("Error stack trace:", ex);
        } finally {
            PathUtils.forceDelete(cloneDir);
        }
    }

    private static void parseFile(Path cloneDir, Path path, GitRepo.GitRepoBuilder repoBuilder) {
        String extension = PathUtils.getExtension(path);
        Language language = extensionToLanguage.get(extension);
        Parser parser;
        File file;
        try {
            parser = Parser.getParser(language);
            file = parser.parse(path);
        } catch (ParsingException ignored) {
            parser = FallbackParser.getInstance();
            file = parser.parse(path);
            file.setLanguage(language);
        }
        file.setPath(FileSystems.getDefault().getSeparator() + cloneDir.relativize(path));
        repoBuilder.file(file);
        repoBuilder.functions(file.getFunctions());
    }
}
