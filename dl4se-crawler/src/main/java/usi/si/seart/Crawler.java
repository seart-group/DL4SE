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
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import usi.si.seart.collection.utils.SetUtils;
import usi.si.seart.http.HttpClient;
import usi.si.seart.http.payload.GhsGitRepo;
import usi.si.seart.io.ExtensionBasedFileVisitor;
import usi.si.seart.model.Language;
import usi.si.seart.model.code.File;
import usi.si.seart.parser.JavaParser;
import usi.si.seart.parser.Parser;
import usi.si.seart.utils.GitUtils;
import usi.si.seart.utils.PathUtils;

import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Collection;
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

    static {
        SessionFactory sessionFactory = new Configuration().configure().buildSessionFactory();
        Session session = sessionFactory.openSession();
        languages = session.createQuery("SELECT l from Language l", Language.class)
                .stream()
                .collect(Collectors.toSet());
        session.close();
    }

    static Set<Language> languages;

    static Set<String> languageNames = languages.stream()
            .map(Language::getName)
            .collect(Collectors.toSet());

    static String[] extensions = languages.stream()
            .map(Language::getExtensions)
            .flatMap(Collection::stream)
            .toArray(String[]::new);

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
            for (Path path : paths) {
                Parser parser = JavaParser.getInstance();
                File file = parser.parse(path);
                file.setPath(FileSystems.getDefault().getSeparator() + cloneDir.relativize(path));
                repoBuilder.file(file);
                repoBuilder.functions(file.getFunctions());
            }
        } catch (GitAPIException ex) {
            log.error("Error while cloning: {}", name);
            log.error("Error stack trace:", ex);
        } finally {
            PathUtils.forceDelete(cloneDir);
        }
    }
}
