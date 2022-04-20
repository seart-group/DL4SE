package usi.si.seart.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import usi.si.seart.exception.EntityNotFoundException;
import usi.si.seart.exception.TokenExpiredException;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerController extends ResponseEntityExceptionHandler {

    @ExceptionHandler(MailException.class)
    @ResponseStatus(code = HttpStatus.SERVICE_UNAVAILABLE)
    public void handleMailException(MailException ex){
        log.error("Mail service error: {}", ex.getMessage());
        log.trace("", ex);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public void handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Integrity constraint violation: {}", ex.getMessage());
        log.trace("", ex);
    }

    @ExceptionHandler(DataAccessException.class)
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public void handleDataAccessException(DataAccessException ex) {
        log.error("Unexpected JDBC exception: {}", ex.getMessage());
        log.trace("", ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public void handleEntityNotFoundException(EntityNotFoundException ex) {
        log.debug(ex.getMessage());
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<?> handleTokenExpiredException(TokenExpiredException ex, WebRequest request) {
        log.debug(ex.getMessage());
        String tokenValue = request.getParameter("token");
        String link = WebMvcLinkBuilder.linkTo(
                WebMvcLinkBuilder.methodOn(UserController.class).resendVerification(tokenValue)
        ).toString();
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(link);
    }
}
