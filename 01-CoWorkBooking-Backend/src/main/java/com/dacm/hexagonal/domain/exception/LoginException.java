package com.dacm.hexagonal.domain.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
public class LoginException extends RuntimeException {

    private HttpStatus status;
    private String message;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy hh:mm:ss")
    private LocalDateTime timestamp;

    public LoginException(String message, Throwable cause, HttpStatus status, LocalDateTime timestamp) {
        super(message, cause);
        this.status = status;
        this.timestamp = timestamp;
    }


}
