package com.dacm.hexagonal.infrastructure.web.dto;

public record UserDtoL(
        String firstName,
        String lastName,
        String email,
        String username,
        String password) {
}
