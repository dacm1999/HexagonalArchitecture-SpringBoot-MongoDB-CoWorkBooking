package com.dacm.hexagonal.domain.exception;

import com.dacm.hexagonal.common.Message;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class RegisterExceptionHandler {

    private HandlerException error;


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<HandlerException> handle(IllegalArgumentException e){
        error = new HandlerException();
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setTimestamp(LocalDateTime.now());

        return  new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
