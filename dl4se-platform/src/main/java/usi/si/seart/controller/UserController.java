package usi.si.seart.controller;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import usi.si.seart.dto.UserDto;
import usi.si.seart.model.user.User;
import usi.si.seart.model.user.token.VerificationToken;
import usi.si.seart.service.EmailService;
import usi.si.seart.service.UserService;
import usi.si.seart.service.VerificationService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    UserService userService;
    VerificationService verificationService;
    EmailService emailService;
    ConversionService conversionService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserDto userDto) {
        HttpStatus status;
        try {
            User created = userService.register(conversionService.convert(userDto, User.class));
            VerificationToken token = verificationService.generate(created);
            String link = WebMvcLinkBuilder.linkTo(
                    WebMvcLinkBuilder.methodOn(UserController.class).verifyUser(token.getValue())
            ).toString();
            emailService.sendVerificationEmail(userDto.getEmail(), link);
            status = HttpStatus.CREATED;
        } catch (DataIntegrityViolationException ignored) {
            status = HttpStatus.FORBIDDEN;
        } catch (DataAccessException ex) {
            log.error("Unexpected JDBC exception", ex);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        } catch (MailException ex) {
            log.error("Mail service exception", ex);
            status = HttpStatus.INTERNAL_SERVER_ERROR;
        }

        return new ResponseEntity<>(status);
    }

    @GetMapping("/verify")
    public ResponseEntity<?> verifyUser(@RequestParam String token) {
        HttpStatus status;
        try {
            verificationService.verify(token);
            status = HttpStatus.OK;
        } catch (IllegalStateException ex) {
            status = HttpStatus.GONE;
        } catch (IllegalArgumentException ex) {
            status = HttpStatus.BAD_REQUEST;
        }

        return new ResponseEntity<>(status);
    }
}
