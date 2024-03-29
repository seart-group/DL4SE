package usi.si.seart.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.si.seart.exception.LanguageNotFoundException;
import usi.si.seart.model.Language;
import usi.si.seart.repository.LanguageRepository;

import java.util.List;

public interface LanguageService {

    Language getWithName(String name);
    List<Language> getAll();

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class LanguageServiceImpl implements LanguageService {

        LanguageRepository languageRepository;

        @Override
        public Language getWithName(String name) {
            return languageRepository.findByName(name)
                    .orElseThrow(() -> new LanguageNotFoundException("name", name));
        }

        @Override
        public List<Language> getAll() {
            return languageRepository.findAll();
        }
    }
}
