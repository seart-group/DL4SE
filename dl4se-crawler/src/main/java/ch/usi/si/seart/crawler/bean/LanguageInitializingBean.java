package ch.usi.si.seart.crawler.bean;

import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.repository.LanguageRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.MultiValueMap;

import java.util.Optional;
import java.util.function.Supplier;

@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class LanguageInitializingBean implements InitializingBean {

    MultiValueMap<String, String> languages;

    @NonFinal
    @Accessors(makeFinal = true)
    @Setter(onMethod_ = @Autowired)
    LanguageRepository languageRepository;

    @Override
    public void afterPropertiesSet() {
        languages.forEach((name, extensions) -> {
            Optional<Language> optional = languageRepository.findByNameIgnoreCase(name);
            Supplier<Language> supplier = () -> Language.builder().name(name).build();
            Language language = optional.orElseGet(supplier);
            language.setExtensions(extensions);
            languageRepository.save(language);
        });
    }
}
