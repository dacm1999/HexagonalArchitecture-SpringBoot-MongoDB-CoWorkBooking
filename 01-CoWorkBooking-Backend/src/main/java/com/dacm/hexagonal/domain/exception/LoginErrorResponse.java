package com.dacm.hexagonal.domain.exception;

public class LoginErrorResponse extends RuntimeException{

    public LoginErrorResponse(String message, Throwable cause) {
        super(message, cause);
    }

}
