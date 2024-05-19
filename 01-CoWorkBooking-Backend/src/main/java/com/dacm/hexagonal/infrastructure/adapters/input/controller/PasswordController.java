package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.PasswordService;
import com.dacm.hexagonal.application.port.in.UserService;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.domain.model.dto.PasswordDto;
import com.dacm.hexagonal.domain.model.dto.PasswordResetDto;
import com.dacm.hexagonal.infrastructure.adapters.output.email.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.mail.MessagingException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/password")
public class PasswordController {

    @Autowired
    private final PasswordService passwordService;

    @Operation(summary = "Reset password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Message.PASSWORD_RESET_EMAIL_SENT),
            @ApiResponse(responseCode = "404", description = Message.PASSWORD_MANDATORY)
    })
    @PutMapping("/reset")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordResetDto request) throws MessagingException {
        return ResponseEntity.ok(passwordService.resetPassword(request));
    }

    @Operation(summary = "Update password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Message.PASSWORD_UPDATED),
            @ApiResponse(responseCode = "404", description = Message.PASSWORD_MANDATORY)
    })
    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updatePassword(@PathVariable String userId, @RequestBody PasswordDto request) throws MessagingException {
        return ResponseEntity.ok(passwordService.updatePassword(userId, request));
    }


}
