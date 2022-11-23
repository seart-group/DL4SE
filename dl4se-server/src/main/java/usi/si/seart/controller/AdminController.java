package usi.si.seart.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import usi.si.seart.exception.ConfigurationNotFoundException;
import usi.si.seart.model.Configuration;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.task.Task_;
import usi.si.seart.model.user.Role;
import usi.si.seart.model.user.User;
import usi.si.seart.model.user.User_;
import usi.si.seart.security.annotation.AdminRestController;
import usi.si.seart.service.ConfigurationService;
import usi.si.seart.service.TaskService;
import usi.si.seart.service.UserService;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@AdminRestController
@RequestMapping("/admin")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AdminController {

    UserService userService;
    TaskService taskService;
    ConfigurationService configurationService;

    @GetMapping
    public ResponseEntity<?> isAdmin() {
        return ResponseEntity.ok().build();
    }

    @GetMapping("/user")
    public ResponseEntity<?> listUsers(
            @SortDefault(sort = User_.REGISTERED, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<User> users = userService.getAll(pageable);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/user/{uid}")
    public ResponseEntity<?> getUser(@PathVariable String uid) {
        User user = userService.getWithUid(uid);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/user/{uid}/enable")
    public ResponseEntity<?> enableUser(@PathVariable String uid) {
        User user = userService.getWithUid(uid);
        user.setEnabled(true);
        userService.update(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/{uid}/disable")
    public ResponseEntity<?> disableUser(@PathVariable String uid) {
        User user = userService.getWithUid(uid);
        user.setEnabled(false);
        userService.update(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/{uid}/promote")
    public ResponseEntity<?> promoteUser(@PathVariable String uid) {
        User user = userService.getWithUid(uid);
        user.setRole(Role.ADMIN);
        userService.update(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/user/{uid}/demote")
    public ResponseEntity<?> demoteUser(@PathVariable String uid) {
        User user = userService.getWithUid(uid);
        user.setRole(Role.USER);
        userService.update(user);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/task")
    public ResponseEntity<?> listTasks(
            @SortDefault(sort = Task_.SUBMITTED, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Task> tasks = taskService.getAll(pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/configuration")
    public ResponseEntity<?> getConfiguration() {
        return ResponseEntity.ok(configurationService.get());
    }

    @PostMapping("/configuration")
    public ResponseEntity<?> updateConfiguration(@RequestBody Map<@NotBlank String, @NotNull Object> requestBody) {
        // FIXME 20.10.22: Validation is not properly performed!
        List<Configuration> configurations = requestBody.entrySet().stream()
                .map(entry -> Configuration.builder().key(entry.getKey()).value(entry.getValue().toString()).build())
                .collect(Collectors.toList());

        for (Configuration configuration: configurations) {
            boolean exists = configurationService.exists(configuration);
            if (!exists) throw new ConfigurationNotFoundException(configuration.getKey());
        }

        return ResponseEntity.ok(configurationService.update(configurations));
    }
}
