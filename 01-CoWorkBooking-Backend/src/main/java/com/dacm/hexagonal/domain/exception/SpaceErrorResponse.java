package com.dacm.hexagonal.domain.exception;

public class SpaceErrorResponse extends RuntimeException{

    public SpaceErrorResponse(String message) {
        super(message);
    }

    public SpaceErrorResponse(String message, Throwable cause) {
        super(message, cause);
    }

    public SpaceErrorResponse(Throwable cause) {
        super(cause);
    }

}
