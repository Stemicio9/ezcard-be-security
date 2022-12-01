package com.metra.ezcardbesecurity.exception;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
public class BaseCustomException extends Exception {

    private final HttpStatus statusCode;

    public BaseCustomException(String message, HttpStatus statusCode) {
        super(message);
        this.statusCode = statusCode;
    }

}
