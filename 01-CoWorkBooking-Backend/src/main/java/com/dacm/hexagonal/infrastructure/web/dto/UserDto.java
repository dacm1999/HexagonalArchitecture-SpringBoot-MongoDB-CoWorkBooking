package com.dacm.hexagonal.infrastructure.web.dto;

public record UserDto(
        String firstName,
        String lastName,
        String email,
        String username,
        String password) {
}
