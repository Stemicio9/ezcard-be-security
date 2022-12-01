package com.metra.ezcardbesecurity.exception;

import org.springframework.http.HttpStatus;

public class TokenNotRefreshedException extends BaseCustomException {

    public TokenNotRefreshedException(String message, HttpStatus statusCode) {
        super(message, statusCode);
    }

}
