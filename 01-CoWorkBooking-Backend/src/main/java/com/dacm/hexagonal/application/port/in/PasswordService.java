package com.dacm.hexagonal.application.port.in;


import com.dacm.hexagonal.domain.model.dto.PasswordDto;
import com.dacm.hexagonal.domain.model.dto.PasswordResetDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import jakarta.mail.MessagingException;

public interface PasswordService {

    ApiResponse resetPassword(PasswordResetDto request) throws MessagingException;
    ApiResponse updatePassword(String userId, PasswordDto request) throws MessagingException;
    String generateRandomPassword(int length);
}
