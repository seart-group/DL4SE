package ch.usi.si.seart.server.controller;

import ch.usi.si.seart.exception.ConfigurationNotFoundException;
import ch.usi.si.seart.model.Configuration;
import ch.usi.si.seart.model.Configuration_;
import ch.usi.si.seart.model.user.Role;
import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.User_;
import ch.usi.si.seart.server.dto.user.UserSearchDto;
import ch.usi.si.seart.server.security.annotation.AdminRestController;
import ch.usi.si.seart.server.service.ConfigurationService;
import ch.usi.si.seart.server.service.UserService;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.SortDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
    ConfigurationService configurationService;
    ConversionService conversionService;

    @GetMapping
    public ResponseEntity<?> isAdmin() {
        return ResponseEntity.ok().build();
    }

    @SuppressWarnings("unchecked")
    @GetMapping("/user")
    public ResponseEntity<?> listUsers(
            UserSearchDto userSearchDto,
            @SortDefault(sort = User_.REGISTERED, direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Specification<User> specification = conversionService.convert(userSearchDto, Specification.class);
        Page<User> users = userService.getAll(specification, pageable);
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
            if (!exists) throw new ConfigurationNotFoundException(Configuration_.key, configuration.getKey());
        }

        return ResponseEntity.ok(configurationService.update(configurations));
    }
}
