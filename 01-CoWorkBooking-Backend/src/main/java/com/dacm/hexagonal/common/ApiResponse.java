package com.dacm.hexagonal.common;


import lombok.Builder;
import org.springframework.http.HttpStatus;


import java.time.LocalDateTime;

public class ApiResponse extends Response {

    public ApiResponse( Integer code, String message,HttpStatus status, LocalDateTime timestamp) {
        super( code,message, status, timestamp);
    }
}
