package com.dacm.hexagonal.domain.model.dto;

public record UserRecord(
        String firstName,
        String lastName,
        String email,
        String username,
        String password) {
}
