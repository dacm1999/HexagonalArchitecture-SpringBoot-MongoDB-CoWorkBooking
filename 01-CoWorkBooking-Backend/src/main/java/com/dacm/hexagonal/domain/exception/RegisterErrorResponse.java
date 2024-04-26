package com.dacm.hexagonal.domain.exception;

public class RegisterErrorResponse extends RuntimeException{

    public RegisterErrorResponse(String message) {
        super(message);
    }

    public RegisterErrorResponse(String message, Throwable cause) {
        super(message, cause);
    }

    public RegisterErrorResponse(Throwable cause) {
        super(cause);
    }
}
