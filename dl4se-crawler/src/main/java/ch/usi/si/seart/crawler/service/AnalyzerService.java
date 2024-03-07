package ch.usi.si.seart.crawler.service;

import ch.usi.si.seart.analyzer.Analyzer;
import ch.usi.si.seart.analyzer.AnalyzerCustomizer;
import ch.usi.si.seart.analyzer.LocalClone;
import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.model.code.File;
import ch.usi.si.seart.model.code.Function;
import ch.usi.si.seart.service.LanguageService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface AnalyzerService {

    CompletableFuture<File> analyze(LocalClone localClone, Path path);

    @Slf4j
    @Service
    @AllArgsConstructor(onConstructor_ = @Autowired)
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    class AnalysisServiceImpl implements AnalyzerService {

        LanguageService languageService;
        TreeSitterVersionService treeSitterVersionService;

        AnalyzerCustomizer<Analyzer> analyzerCustomizer;

        @Override
        @Async("analyzerExecutor")
        public CompletableFuture<File> analyze(LocalClone localClone, Path path) {
            Path relative = localClone.relativePathOf(path);
            log.debug("Analyzing file: {}", relative);
            Language language = languageService.getAssociatedWith(path);
            try (Analyzer analyzer = new Analyzer(localClone, path)) {
                analyzerCustomizer.customize(analyzer);
                Analyzer.Result result = analyzer.analyze();
                File file = result.getFile();
                List<Function> functions = result.getFunctions();
                file.setLanguage(language);
                file.setTreeSitterVersion(treeSitterVersionService.getCurrentVersion());
                functions.forEach(function -> function.setLanguage(language));
                return CompletableFuture.completedFuture(file);
            } catch (Exception ex) {
                log.error("Exception occurred while analyzing: {}", relative, ex);
                return CompletableFuture.failedFuture(ex);
            }
        }
    }
}
