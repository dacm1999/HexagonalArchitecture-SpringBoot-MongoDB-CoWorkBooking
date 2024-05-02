package com.dacm.hexagonal.domain.model;

import com.dacm.hexagonal.domain.enums.Role;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class User {

    private String id;
    private String firstName;
    private String lastName;
    private String email;
    private String username;
    private String password;
    private Role role;

    public User withId(String id) {
        return new User(id, this.firstName, this.lastName, this.email, this.username, this.password, this.role);
    }
}
