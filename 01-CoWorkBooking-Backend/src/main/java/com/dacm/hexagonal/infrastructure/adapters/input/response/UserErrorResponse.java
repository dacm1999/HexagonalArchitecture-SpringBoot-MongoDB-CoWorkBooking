package com.dacm.hexagonal.infrastructure.adapters.input.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserErrorResponse {
    String username;
    String email;
    String errorDescription;

    public UserErrorResponse(String username, String email, String errorDescription) {
        this.username = username;
        this.email = email;
        this.errorDescription = errorDescription;
    }
}
