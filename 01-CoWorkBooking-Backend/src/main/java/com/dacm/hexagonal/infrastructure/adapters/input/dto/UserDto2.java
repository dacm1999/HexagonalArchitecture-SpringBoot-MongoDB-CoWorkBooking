package com.dacm.hexagonal.infrastructure.adapters.input.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UserDto2 {

    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
}
