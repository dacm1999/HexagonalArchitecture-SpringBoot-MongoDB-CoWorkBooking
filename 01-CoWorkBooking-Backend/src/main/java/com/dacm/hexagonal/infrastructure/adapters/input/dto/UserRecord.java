package com.dacm.hexagonal.infrastructure.adapters.input.dto;

public record UserRecord(
        String firstName,
        String lastName,
        String email,
        String username,
        String password) {
}
