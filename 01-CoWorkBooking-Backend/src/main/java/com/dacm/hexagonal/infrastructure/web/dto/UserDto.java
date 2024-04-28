package com.dacm.hexagonal.infrastructure.web.dto;

import lombok.*;

@Data
@AllArgsConstructor
public class UserDto {

    private String firstName;
    private String lastName;
    private String email;
    private String username;

}
