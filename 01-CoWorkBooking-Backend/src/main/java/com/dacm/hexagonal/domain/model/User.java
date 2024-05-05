package com.dacm.hexagonal.domain.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    private String firstName;
    private String lastName;
    private String email;
    private String userId;
    private String password;

}
