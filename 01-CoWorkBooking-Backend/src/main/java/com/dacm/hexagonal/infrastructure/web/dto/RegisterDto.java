package com.dacm.hexagonal.infrastructure.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class RegisterDto {

    private String username;
    private String password;
    private String firstname;
    private String lastname;
    private String email;

}
