package usi.si.seart.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usi.si.seart.model.Language;
import usi.si.seart.model.user.User;
import usi.si.seart.security.UserPrincipal;
import usi.si.seart.service.StatisticsService;
import usi.si.seart.service.UserService;

import java.util.Map;
import java.util.TreeMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/statistics")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class StatisticsController {

    UserService userService;
    StatisticsService statisticsService;

    @GetMapping("/code")
    public ResponseEntity<?> codeSize() {
        return ResponseEntity.ok(statisticsService.codeSize());
    }

    @GetMapping("/users")
    public ResponseEntity<?> users() {
        return ResponseEntity.ok(statisticsService.countUsers());
    }

    @GetMapping("/repos")
    public ResponseEntity<?> repos() {
        return ResponseEntity.ok(statisticsService.countGitRepos());
    }

    @GetMapping("/files")
    public ResponseEntity<?> files() {
        return ResponseEntity.ok(statisticsService.countFiles());
    }

    @GetMapping("/functions")
    public ResponseEntity<?> functions() {
        return ResponseEntity.ok(statisticsService.countFunctions());
    }

    @GetMapping("/languages/repos")
    public ResponseEntity<?> languagesRepos() {
        return ResponseEntity.ok(
                extractCounts(statisticsService::countGitReposByLanguage)
        );
    }

    @GetMapping("/languages/files")
    public ResponseEntity<?> languagesFiles() {
        return ResponseEntity.ok(
                extractCounts(statisticsService::countFilesByLanguage)
        );
    }

    @GetMapping("/languages/functions")
    public ResponseEntity<?> languagesFunctions() {
        return ResponseEntity.ok(
                extractCounts(statisticsService::countFunctionsByLanguage)
        );
    }

    private Map<String, Long> extractCounts(Supplier<Map<Language, Long>> supplier) {
        return supplier.get().entrySet().stream()
                .map(entry -> Map.entry(entry.getKey().getName(), entry.getValue()))
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (v1, v2) -> v2, TreeMap::new));
    }

    @GetMapping("/tasks")
    public ResponseEntity<?> tasks() {
        return ResponseEntity.ok(statisticsService.countTasks());
    }

    @GetMapping("/tasks/status")
    public ResponseEntity<?> tasksStatus() {
        return ResponseEntity.ok(statisticsService.countTasksByStatus());
    }

    @GetMapping("/tasks/size")
    public ResponseEntity<?> tasksSize() {
        return ResponseEntity.ok(statisticsService.getTotalTaskSize());
    }

    @GetMapping("/user/tasks")
    public ResponseEntity<?> userTasks(@AuthenticationPrincipal UserPrincipal principal) {
        User requester = userService.getWithId(principal.getId());
        return ResponseEntity.ok(statisticsService.countTasks(requester));
    }

    @GetMapping("/user/tasks/status")
    public ResponseEntity<?> userTaskStats(@AuthenticationPrincipal UserPrincipal principal) {
        User requester = userService.getWithId(principal.getId());
        return ResponseEntity.ok(statisticsService.countTasksByStatus(requester));
    }

    @GetMapping("/user/tasks/size")
    public ResponseEntity<?> userTaskSize(@AuthenticationPrincipal UserPrincipal principal) {
        User requester = userService.getWithId(principal.getId());
        return ResponseEntity.ok(statisticsService.getTotalTaskSize(requester));
    }
}
