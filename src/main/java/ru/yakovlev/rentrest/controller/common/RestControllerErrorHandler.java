package ru.yakovlev.rentrest.controller.common;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.yakovlev.rentrest.exception.AccessRuntimeException;
import ru.yakovlev.rentrest.exception.BusinessRuntimeException;
import ru.yakovlev.rentrest.exception.SecurityRuntimeException;
import ru.yakovlev.rentrest.model.dto.error.ErrorDto;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class RestControllerErrorHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage());

        Map<String, String> errors = new HashMap<>();

        exception.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        return errors;
    }

    @ExceptionHandler({
            ExpiredJwtException.class,
            UnsupportedJwtException.class,
            MalformedJwtException.class,
            SignatureException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorDto handleJwtRuntimeException() {
        log.error("Невалидный токен");
        return new ErrorDto("Невалидный токен");
    }

    @ExceptionHandler(SecurityRuntimeException.class)
    public ResponseEntity handleSecurityRuntimeException(SecurityRuntimeException exception) {
        log.error(exception.getMessage());
        return ResponseEntity.status(exception.getHttpStatus()).body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler(BusinessRuntimeException.class)
    public ResponseEntity handleBusinessRuntimeException(BusinessRuntimeException exception) {
        log.error(exception.getMessage());
        return ResponseEntity.status(exception.getHttpStatus()).body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler(AccessRuntimeException.class)
    public ResponseEntity handleAccessRuntimeException(AccessRuntimeException exception) {
        log.error(exception.getMessage());
        return ResponseEntity.status(exception.getHttpStatus()).body(new ErrorDto(exception.getMessage()));
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity handleThrowableRuntimeException(Throwable exception) {
        log.error(exception.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ErrorDto("Ошибка сервера"));
    }
}
