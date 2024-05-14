package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.RegisterService;
import com.dacm.hexagonal.domain.model.dto.RegisterDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.JwtLoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RegisterControllerTest {

    private static final String REGISTER_URL = "/api/v1/register";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Mock
    RegisterController registerController;

    @Mock
    RegisterService registerService;
    @Qualifier("objectMapper")
    @Autowired
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
        registerController = new RegisterController(registerService);
    }

    @Test
    @DisplayName("Test register")
    void register() throws Exception {
        // Given
        RegisterDto registerDto = RegisterDto.builder()
                .userId("testUser")
                .password("testPassword")
                .firstname("testFirstName")
                .lastname("testLastName")
                .email("testEmail")
                .build();

        JwtLoginResponse jwtLoginResponse = JwtLoginResponse.builder()
                .token("eyJhbGciOiJIUzI1NiJ9.eyJmaXJzdE5hbWUiOiJEYW5pZWwiLCJsYXN0TmFtZSI6IkNvbnRyZXJhcyIsInJvbGUiOiJST0xFX0FETUlOIiwic3ViIjoiZGFjbSIsImlhdCI6MTcxNTYzMDMxMSwiZXhwIjoxNzE1NjMzOTExfQ.JJmwry8poag_m2SDB-oC_PbjG1MPWg4XTqCfyOv3_l8")
                .build();

        // Mocking the behavior of the register service
        when(registerService.signUp(ArgumentMatchers.any(RegisterDto.class))).thenReturn(jwtLoginResponse);

        // Setting up the controller for testing
        mockMvc = MockMvcBuilders.standaloneSetup(registerController).build();

        // When/Then
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDto)))
                .andExpect(status().isOk())
                .andReturn();

        // Asserting the status code
//        int statusCode = result.getResponse().getStatus();
//        assert statusCode == 200 : "Expected status code 200 but got " + statusCode;
    }

}