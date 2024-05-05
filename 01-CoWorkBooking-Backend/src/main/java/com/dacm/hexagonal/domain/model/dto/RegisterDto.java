package com.dacm.hexagonal.domain.model.dto;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterDto {

    private String userId;
    private String password;
    private String firstname;
    private String lastname;
    private String email;

}
