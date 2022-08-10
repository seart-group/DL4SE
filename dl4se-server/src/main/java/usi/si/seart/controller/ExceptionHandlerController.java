package usi.si.seart.controller;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import usi.si.seart.exception.EntityNotFoundException;
import usi.si.seart.exception.TokenExpiredException;

import java.io.FileNotFoundException;

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
    public ResponseEntity<?> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Integrity constraint violation: {}", ex.getMessage());
        log.trace("", ex);
        Throwable cause = ex.getCause();
        if (cause instanceof ConstraintViolationException) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
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
        log.debug("Entity not found: {}", ex.getMessage());
        log.trace("", ex);
    }

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(code = HttpStatus.NOT_FOUND)
    public void handleFileNotFoundException(FileNotFoundException ex) {
        log.debug("File not found: {}", ex.getMessage());
        log.trace("", ex);
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(code = HttpStatus.FORBIDDEN)
    public void handleTokenExpiredException(TokenExpiredException ex) {
        log.debug("Verification exception: {}", ex.getMessage());
        log.trace("", ex);
    }
}
