package com.dacm.hexagonal.domain.exception;


import com.dacm.hexagonal.common.Message;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<LoginException> handleHttpClientErrorException(BadCredentialsException ex) {

        LoginException responseError = new LoginException();

        responseError.setStatus(HttpStatus.BAD_REQUEST);
        responseError.setMessage(Message.LOGIN_INVALID_USERNAME_OR_PASSWORD);
        responseError.setTimestamp(LocalDateTime.now());


//        LoginException error = new LoginException(
//                "asfasfasf",
//                ex.getCause(),
//                HttpStatus.BAD_REQUEST,
//                LocalDateTime.now()
//        );
        return new ResponseEntity<>(responseError, HttpStatus.BAD_REQUEST);
    }



}

