package com.dacm.hexagonal.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class SpaceExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<HandlerException> handle(RuntimeException e){
        HandlerException error = new HandlerException();
        error.setStatusCode(HttpStatus.NOT_FOUND.value());
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.NOT_FOUND);
        error.setTimestamp(LocalDateTime.now());

        return  new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }
}
