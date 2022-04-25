package usi.si.seart.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import usi.si.seart.dto.ConfigurationDto;
import usi.si.seart.model.Configuration;
import usi.si.seart.model.user.User;
import usi.si.seart.service.ConfigurationService;
import usi.si.seart.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/admin")
@PreAuthorize("hasRole('ADMIN')")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class AdminController {

    UserService userService;
    ConversionService conversionService;
    ConfigurationService configurationService;

    @PostMapping("/{id}/enable")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        User user = userService.getWithId(id);
        user.setEnabled(true);
        userService.update(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/disable")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        User user = userService.getWithId(id);
        user.setEnabled(false);
        userService.update(user);
        return ResponseEntity.ok().build();
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
