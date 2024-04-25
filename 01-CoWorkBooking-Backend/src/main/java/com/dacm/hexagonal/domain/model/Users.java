package com.dacm.hexagonal.domain.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Users {

    private String id;
    private String name;
    private String email;
    private String password;
    private String role;

}
