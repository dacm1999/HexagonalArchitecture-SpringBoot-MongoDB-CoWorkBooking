package com.dacm.hexagonal.domain.exception;


import com.dacm.hexagonal.common.Message;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class LoginExceptionHandler {

    private HandlerException error;


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<HandlerException> handle(BadCredentialsException e){
        error = new HandlerException();
        error.setStatusCode(HttpStatus.NOT_FOUND.value());
        error.setMessage(Message.LOGIN_INVALID_USERNAME_OR_PASSWORD);
        error.setStatus(HttpStatus.NOT_FOUND);
        error.setTimestamp(LocalDateTime.now());

        return  new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }



}

