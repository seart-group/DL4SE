package usi.si.seart.crawler.service;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import usi.si.seart.crawler.repository.LanguageRepository;
import usi.si.seart.model.Language;

import java.util.List;

public interface LanguageService {

    List<Language> getAll();

    @Service
    @FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
    @AllArgsConstructor(onConstructor_ = @Autowired)
    class LanguageServiceImpl implements LanguageService {

        LanguageRepository languageRepository;

        @Override
        public List<Language> getAll() {
            return languageRepository.findAll();
        }
    }
}
