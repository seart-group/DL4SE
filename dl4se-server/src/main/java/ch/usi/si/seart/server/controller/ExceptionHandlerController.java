package ch.usi.si.seart.server.controller;

import ch.usi.si.seart.server.exception.EntityNotFoundException;
import ch.usi.si.seart.server.exception.TokenExpiredException;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.FileNotFoundException;
import java.util.AbstractMap;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request
    ) {
        Map<String, String> errors = ex.getFieldErrors().stream()
                .map(error -> new AbstractMap.SimpleEntry<>(
                        error.getField(),
                        error.getDefaultMessage()
                ))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue
                ));
        Map<String, Map<String, String>> payload = Map.of("errors", errors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(payload);
    }
}
