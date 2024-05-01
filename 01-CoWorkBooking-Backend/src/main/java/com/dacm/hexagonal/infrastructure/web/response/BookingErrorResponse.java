package com.dacm.hexagonal.infrastructure.web.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingErrorResponse {

    String username;
    String errorDescription;

    public BookingErrorResponse(String username, String errorDescription) {
        this.username = username;
        this.errorDescription = errorDescription;
    }
}
