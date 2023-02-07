package ru.yakovlev.rentrest.exception;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class AccessRuntimeException extends RuntimeException{
    private HttpStatus httpStatus;

    public AccessRuntimeException(String message, HttpStatus httpStatus) {
        super(message);

        this.httpStatus = httpStatus;
    }
}
