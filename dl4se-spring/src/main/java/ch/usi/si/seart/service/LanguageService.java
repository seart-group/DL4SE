package ch.usi.si.seart.service;

import ch.usi.si.seart.exception.LanguageNotFoundException;
import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.model.Language_;
import ch.usi.si.seart.repository.LanguageExtensionRepository;
import ch.usi.si.seart.repository.LanguageRepository;
import com.google.common.io.Files;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public interface LanguageService {

    List<Language> getAll();

    Language getAssociatedWith(Path path);

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class LanguageServiceImpl implements LanguageService {

        LanguageRepository languageRepository;
        LanguageExtensionRepository languageExtensionRepository;

        @Override
        public List<Language> getAll() {
            return languageRepository.findAll();
        }

        @Override
        public Language getAssociatedWith(Path path) {
            String extension = Files.getFileExtension(path.toString());
            return languageExtensionRepository.findLanguageByExtension(extension)
                    .orElseThrow(() -> new LanguageNotFoundException(
                            Language_.extensions,
                            Collections.singletonList(extension)
                    ));
        }
    }
}
