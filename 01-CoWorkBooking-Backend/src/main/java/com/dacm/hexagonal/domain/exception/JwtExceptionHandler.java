package com.dacm.hexagonal.domain.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.DecodingException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.io.IOException;
import java.security.SignatureException;
import java.time.LocalDateTime;

@RestControllerAdvice
@RequiredArgsConstructor
public class JwtExceptionHandler {

    private HandlerException error;


    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<HandlerException> handle(ExpiredJwtException e) {
        error = new HandlerException();
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<HandlerException> handle(MalformedJwtException e) {
        error = new HandlerException();
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DecodingException.class)
    public ResponseEntity<HandlerException> handle(DecodingException e) {
        error = new HandlerException();
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<HandlerException> handle(SignatureException e) {
        error = new HandlerException();
        error.setStatusCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(e.getMessage());
        error.setStatus(HttpStatus.BAD_REQUEST);
        error.setTimestamp(LocalDateTime.now());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}
