package usi.si.seart.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@RestController
public class RootController {
    @RequestMapping("/")
    public String root() {
        return "Spring Boot is running: " + ZonedDateTime.now(ZoneOffset.UTC);
    }
}
