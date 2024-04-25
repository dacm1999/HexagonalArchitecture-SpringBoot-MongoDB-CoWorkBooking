package com.dacm.hexagonal.domain.exception;

public class BookingErrorResponse extends RuntimeException{

    public BookingErrorResponse(String message) {
        super(message);
    }

    public BookingErrorResponse(String message, Throwable cause) {
        super(message, cause);
    }

    public BookingErrorResponse(Throwable cause) {
        super(cause);
    }

}
