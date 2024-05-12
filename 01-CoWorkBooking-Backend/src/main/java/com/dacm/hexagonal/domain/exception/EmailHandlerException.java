package com.dacm.hexagonal.domain.exception;

import jakarta.mail.MessagingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.thymeleaf.exceptions.TemplateInputException;

import java.time.LocalDateTime;

@RestControllerAdvice
public class EmailHandlerException{

    @ExceptionHandler(MessagingException.class)
    public ResponseEntity<HandlerException> handle(MessagingException e){
        HandlerException error = new HandlerException();
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setTimestamp(LocalDateTime.now());

        return  new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TemplateInputException.class)
    public ResponseEntity<HandlerException> handle(TemplateInputException e){
        HandlerException error = new HandlerException();
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setTimestamp(LocalDateTime.now());

        return  new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

}
