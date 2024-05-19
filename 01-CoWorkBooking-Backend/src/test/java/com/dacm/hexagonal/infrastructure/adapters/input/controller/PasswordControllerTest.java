package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.PasswordService;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.domain.model.dto.PasswordDto;
import com.dacm.hexagonal.domain.model.dto.PasswordResetDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class PasswordControllerTest {


    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    PasswordController passwordController;

    @MockBean
    PasswordService passwordService;

    @Autowired
    WebApplicationContext webApplicationContext;

    private static final String BASE_URL = "/api/v1/password";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        passwordController = new PasswordController(passwordService);
        passwordService = mock(PasswordService.class);
    }

    @Test
    @DisplayName("Test reset password")
    void resetPassword() throws Exception {
        PasswordResetDto passwordResetDto = new PasswordResetDto("dacm.dev@icloud.com");

        ApiResponse apiResponse = new ApiResponse(200, Message.PASSWORD_RESET_EMAIL_SENT, HttpStatus.OK, LocalDateTime.now(), null);

//        when(passwordService.resetPassword(ArgumentMatchers.any(PasswordResetDto.class))).thenReturn(apiResponse);
        when(passwordService.resetPassword((passwordResetDto))).thenReturn(apiResponse);
        mockMvc = MockMvcBuilders.standaloneSetup(passwordController).build();

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/reset")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordResetDto)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Test update password")
    void updatePassword() throws Exception {
        String userId = "dacm";
        PasswordDto passwordDto = new PasswordDto("password", "newPassword");

        ApiResponse apiResponse = new ApiResponse(200, Message.PASSWORD_RESET_EMAIL_SENT, HttpStatus.OK, LocalDateTime.now(), null);

        when(passwordService.updatePassword(ArgumentMatchers.any(String.class),ArgumentMatchers.any(PasswordDto.class))).thenReturn(apiResponse);

        mockMvc = MockMvcBuilders.standaloneSetup(passwordController).build();

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/update/" + userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(passwordDto)))
                .andExpect(status().isOk())
                .andReturn();
    }
}