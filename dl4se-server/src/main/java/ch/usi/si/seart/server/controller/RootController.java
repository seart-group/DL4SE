package ch.usi.si.seart.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping(value = "favicon.ico")
    public void favicon() {
    }

    @GetMapping
    public ResponseEntity<?> root() {
        return ResponseEntity.ok().build();
    }
}
