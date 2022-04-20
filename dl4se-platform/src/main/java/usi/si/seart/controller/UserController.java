package usi.si.seart.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usi.si.seart.dto.LoginDto;
import usi.si.seart.dto.UserDto;
import usi.si.seart.model.user.User;
import usi.si.seart.model.user.token.Token;
import usi.si.seart.security.jwt.JwtTokenProvider;
import usi.si.seart.service.EmailService;
import usi.si.seart.service.UserService;
import usi.si.seart.service.VerificationService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    AuthenticationManager authenticationManager;

    JwtTokenProvider tokenProvider;

    UserService userService;
    VerificationService verificationService;
    EmailService emailService;
    ConversionService conversionService;

    @PostMapping("/login")
    public ResponseEntity<?> logIn(@Valid @RequestBody LoginDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok("Bearer " + token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody UserDto userDto) {
        User created = userService.create(conversionService.convert(userDto, User.class));
        Token token = verificationService.generate(created);
        String link = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).verify(token.getValue())
        ).toString();
        emailService.sendVerificationEmail(userDto.getEmail(), link);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam String token) {
        verificationService.verify(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify/resend")
    public ResponseEntity<?> resendVerification(@RequestParam String token) {
        Token refreshed = verificationService.refresh(token);
        String link = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).verify(refreshed.getValue())
        ).toString();
        emailService.sendVerificationEmail(refreshed.getUser().getEmail(), link);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listUsers(
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "registered") String column
    ) {
        List<User> users = userService.getAll(page, column);
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') || authentication.principal.id == #id")
    public ResponseEntity<?> getUser(@PathVariable Long id) {
        User user = userService.getWithId(id);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/{id}/enable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        User user = userService.getWithId(id);
        user.setEnabled(true);
        userService.update(user);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/disable")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        User user = userService.getWithId(id);
        user.setEnabled(false);
        userService.update(user);
        return ResponseEntity.ok().build();
    }
}
