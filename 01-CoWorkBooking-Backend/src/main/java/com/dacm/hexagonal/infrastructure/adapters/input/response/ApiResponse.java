package com.dacm.hexagonal.infrastructure.adapters.input.response;


import org.springframework.http.HttpStatus;


import java.time.LocalDateTime;

public class ApiResponse extends Response {

    public ApiResponse( Integer code, String message,HttpStatus status, LocalDateTime timestamp) {
        super( code,message, status, timestamp);
    }
}
