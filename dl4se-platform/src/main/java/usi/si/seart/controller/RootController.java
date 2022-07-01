package usi.si.seart.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

    @GetMapping
    public ResponseEntity<?> api() {
        return ResponseEntity.ok().build();
    }
}
