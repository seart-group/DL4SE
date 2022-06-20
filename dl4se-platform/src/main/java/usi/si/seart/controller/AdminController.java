package usi.si.seart.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.SortDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import usi.si.seart.dto.ConfigurationDto;
import usi.si.seart.model.Configuration;
import usi.si.seart.model.task.Task;
import usi.si.seart.model.user.Role;
import usi.si.seart.model.user.User;
import usi.si.seart.security.annotation.AdminRestController;
import usi.si.seart.service.ConfigurationService;
import usi.si.seart.service.TaskService;
import usi.si.seart.service.UserService;

import javax.validation.Valid;

@AdminRestController
@RequestMapping("/admin")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AdminController {

    UserService userService;
    TaskService taskService;
    ConversionService conversionService;
    ConfigurationService configurationService;

    @GetMapping("/user")
    public ResponseEntity<?> listUsers(
            @SortDefault(sort = "registered", direction = Sort.Direction.DESC) Pageable pageable
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

    @GetMapping("/tasks")
    public ResponseEntity<?> listTasks(
            @SortDefault(sort = "submitted", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<Task> tasks = taskService.getAll(pageable);
        return ResponseEntity.ok(tasks);
    }

    @GetMapping("/stats/tasks")
    public ResponseEntity<?> statsTasks() {
        return ResponseEntity.ok(taskService.getSummary());
    }

    @PostMapping("/configuration")
    public ResponseEntity<?> updateConfiguration(@Valid @RequestBody ConfigurationDto configurationDto) {
        Configuration configuration = conversionService.convert(configurationDto, Configuration.class);
        boolean exists = configurationService.exists(configuration);
        if (!exists) ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        configuration = configurationService.modify(configuration);
        return ResponseEntity.ok(configuration);
    }
}
