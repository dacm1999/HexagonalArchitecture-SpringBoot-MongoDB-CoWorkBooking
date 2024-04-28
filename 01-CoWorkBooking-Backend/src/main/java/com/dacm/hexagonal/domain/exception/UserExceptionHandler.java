package com.dacm.hexagonal.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class UserExceptionHandler {


    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<HandlerException> handle(UsernameNotFoundException e){
        HandlerException error = new HandlerException();
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setTimestamp(LocalDateTime.now());

        return  new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }


}
