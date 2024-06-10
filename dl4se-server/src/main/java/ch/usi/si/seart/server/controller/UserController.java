package ch.usi.si.seart.server.controller;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.PasswordResetToken;
import ch.usi.si.seart.model.user.token.VerificationToken;
import ch.usi.si.seart.server.dto.LoginDto;
import ch.usi.si.seart.server.dto.RegisterDto;
import ch.usi.si.seart.server.dto.user.EmailDto;
import ch.usi.si.seart.server.dto.user.PasswordDto;
import ch.usi.si.seart.server.security.TokenProvider;
import ch.usi.si.seart.server.security.UserPrincipal;
import ch.usi.si.seart.server.service.EmailService;
import ch.usi.si.seart.server.service.TokenService;
import ch.usi.si.seart.server.service.UserService;
import ch.usi.si.seart.validation.constraints.OWASPEmail;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Validated
@RestController
@RequestMapping("/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    AuthenticationManager authenticationManager;

    TokenProvider tokenProvider;

    UserService userService;

    TokenService<VerificationToken> verificationService;
    TokenService<PasswordResetToken> passwordResetService;

    EmailService emailService;
    ConversionService conversionService;
    UserDetailsPasswordService userDetailsPasswordService;

    @GetMapping
    public ResponseEntity<?> currentUser(@AuthenticationPrincipal UserPrincipal principal) {
        User requester = userService.getWithId(principal.getId());
        return ResponseEntity.ok(requester);
    }

    @GetMapping("/{uid}")
    @PreAuthorize("authentication.principal.uid == #uid")
    public ResponseEntity<?> getUser(@PathVariable String uid) {
        User user = userService.getWithUid(uid);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<?> logIn(@Valid @RequestBody LoginDto loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generate(authentication);
        return ResponseEntity.ok("Bearer " + token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDto dto) {
        User created = userService.create(conversionService.convert(dto, User.class));
        VerificationToken token = verificationService.generate(created);
        emailService.sendVerificationEmail(token);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam @NotBlank String token) {
        User requester = verificationService.getOwner(token);
        verificationService.verify(token);
        requester.setVerified(true);
        userService.update(requester);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify/resend")
    public ResponseEntity<?> resendVerification(@RequestParam @NotBlank String token) {
        VerificationToken refreshed = verificationService.refresh(token);
        emailService.sendVerificationEmail(refreshed);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/forgotten")
    public ResponseEntity<?> forgottenPassword(@Valid @RequestBody EmailDto dto) {
        User requester = userService.getWithEmail(dto.getEmail());
        PasswordResetToken token = passwordResetService.generate(requester);
        emailService.sendPasswordResetEmail(token);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(@RequestParam @NotBlank String token, @Valid @RequestBody PasswordDto dto) {
        User requester = passwordResetService.getOwner(token);
        passwordResetService.verify(token);
        UserPrincipal principal = conversionService.convert(requester, UserPrincipal.class);
        userDetailsPasswordService.updatePassword(principal, dto.getPassword());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/uid")
    public ResponseEntity<?> updateUsername(
            @NotBlank @Pattern(regexp = "^[a-zA-Z0-9_-]{3,64}$") @RequestBody String uid,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User requester = userService.getWithId(principal.getId());
        if (!requester.getUid().equals(uid)) {
            requester.setUid(uid);
            userService.update(requester);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/email")
    public ResponseEntity<?> updateEmail(
            @NotBlank @OWASPEmail @RequestBody String email,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User requester = userService.getWithId(principal.getId());
        if (!requester.getEmail().equals(email)) {
            requester.setEmail(email);
            requester.setVerified(false);
            userService.update(requester);
            VerificationToken token = verificationService.generate(requester);
            emailService.sendVerificationEmail(token);
        }
        return ResponseEntity.ok().build();
    }

    @PutMapping("/organisation")
    public ResponseEntity<?> updateOrganisation(
            @NotBlank @RequestBody String organisation,
            @AuthenticationPrincipal UserPrincipal principal
    ) {
        User requester = userService.getWithId(principal.getId());
        if (!requester.getOrganisation().equals(organisation)) {
            requester.setOrganisation(organisation);
            userService.update(requester);
        }
        return ResponseEntity.ok().build();
    }
}
