package ru.yakovlev.rentrest.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class SecurityRuntimeException extends RuntimeException{
    private HttpStatus httpStatus;

    public SecurityRuntimeException(String message, HttpStatus httpStatus) {
        super(message);

        this.httpStatus = httpStatus;
    }
}
