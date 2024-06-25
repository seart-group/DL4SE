package ch.usi.si.seart.server.controller;

import ch.usi.si.seart.server.service.CodeService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/code")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class CodeController {

    CodeService codeService;

    @GetMapping("/{id}")
    public ResponseEntity<?> code(@PathVariable Long id) {
        return ResponseEntity.ok(codeService.getWithId(id));
    }
}
