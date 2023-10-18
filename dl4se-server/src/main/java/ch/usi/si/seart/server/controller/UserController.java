package ch.usi.si.seart.server.controller;

import ch.usi.si.seart.model.user.User;
import ch.usi.si.seart.model.user.token.Token;
import ch.usi.si.seart.server.dto.LoginDto;
import ch.usi.si.seart.server.dto.RegisterDto;
import ch.usi.si.seart.server.dto.user.EmailDto;
import ch.usi.si.seart.server.dto.user.PasswordDto;
import ch.usi.si.seart.server.security.UserPrincipal;
import ch.usi.si.seart.server.security.jwt.JwtTokenProvider;
import ch.usi.si.seart.server.service.EmailService;
import ch.usi.si.seart.server.service.PasswordResetService;
import ch.usi.si.seart.server.service.UserService;
import ch.usi.si.seart.server.service.VerificationService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.net.MalformedURLException;

@Slf4j
@RestController
@RequestMapping("/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    AuthenticationManager authenticationManager;

    JwtTokenProvider tokenProvider;

    UserService userService;
    VerificationService verificationService;
    PasswordResetService passwordResetService;
    EmailService emailService;
    ConversionService conversionService;

    @NonFinal
    @Value("${website.url}")
    String websiteUrl;

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
        String token = tokenProvider.generateToken(authentication);
        return ResponseEntity.ok("Bearer " + token);
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterDto dto) {
        User created = userService.create(conversionService.convert(dto, User.class));
        Token token = verificationService.generate(created);
        String link = getVerificationURL(token);
        emailService.sendVerificationEmail(dto.getEmail(), link);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verify(@RequestParam @NotBlank String token) {
        verificationService.verify(token);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/verify/resend")
    public ResponseEntity<?> resendVerification(@RequestParam @NotBlank String token) {
        Token refreshed = verificationService.refresh(token);
        String link = getVerificationURL(refreshed);
        emailService.sendVerificationEmail(refreshed.getUser().getEmail(), link);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/forgotten")
    public ResponseEntity<?> forgottenPassword(@Valid @RequestBody EmailDto dto) {
        User requester = userService.getWithEmail(dto.getEmail());
        Token token = passwordResetService.generate(requester);
        String link = getPasswordResetUrl(token);
        emailService.sendPasswordResetEmail(dto.getEmail(), link);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/password/reset")
    public ResponseEntity<?> resetPassword(
            @RequestParam @NotBlank String token, @Valid @RequestBody PasswordDto dto
    ) {
        passwordResetService.verify(token, dto.getPassword());
        return ResponseEntity.ok().build();
    }

    @SneakyThrows(MalformedURLException.class)
    private String getVerificationURL(Token token) {
        return UriComponentsBuilder.fromHttpUrl(websiteUrl)
                .path("/verify/" + token.getValue())
                .build()
                .toUri()
                .toURL()
                .toString();
    }

    @SneakyThrows(MalformedURLException.class)
    private String getPasswordResetUrl(Token token) {
        return UriComponentsBuilder.fromHttpUrl(websiteUrl)
                .path("/password/reset/" + token.getValue())
                .build()
                .toUri()
                .toURL()
                .toString();
    }
}
