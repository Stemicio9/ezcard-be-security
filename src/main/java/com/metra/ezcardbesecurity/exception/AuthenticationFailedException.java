package com.metra.ezcardbesecurity.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends BaseCustomException {

    public AuthenticationFailedException(String message, HttpStatus statusCode) {
        super(message, statusCode);
    }

}
