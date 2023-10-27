package ch.usi.si.seart.server.controller;

import ch.usi.si.seart.model.Language;
import ch.usi.si.seart.service.LanguageService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/language")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class LanguageController {

    LanguageService languageService;

    @GetMapping
    public ResponseEntity<?> languages() {
        List<Language> languages = languageService.getAll();
        return ResponseEntity.ok(
                languages.stream().map(Language::getName).collect(Collectors.toList())
        );
    }
}
