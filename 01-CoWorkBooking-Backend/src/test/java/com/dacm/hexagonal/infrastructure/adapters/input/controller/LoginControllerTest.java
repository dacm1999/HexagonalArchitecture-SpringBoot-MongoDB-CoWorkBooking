package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.LoginService;
import com.dacm.hexagonal.domain.model.Login;
import com.dacm.hexagonal.infrastructure.adapters.input.response.JwtLoginResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
class LoginControllerTest {

    private static final String BASE_URL = "/api/v1/login";
    private LoginController loginController;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    private LoginService loginService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
        loginService = mock(LoginService.class);
        loginController = new LoginController(loginService);
    }

    @Test
    public void testLoginEndpoint() throws Exception {
        // Given
        Login login = new Login();
        login.setUserId("dacm");
        login.setPassword("1234");


        JwtLoginResponse jwtLoginResponse = JwtLoginResponse.builder()
                .token("eyJhbGciOiJIUzI1NiJ9.eyJmaXJzdE5hbWUiOiJEYW5pZWwiLCJsYXN0TmFtZSI6IkNvbnRyZXJhcyIsInJvbGUiOiJST0xFX0FETUlOIiwic3ViIjoiZGFjbSIsImlhdCI6MTcxNTYzMDMxMSwiZXhwIjoxNzE1NjMzOTExfQ.JJmwry8poag_m2SDB-oC_PbjG1MPWg4XTqCfyOv3_l8")
                .build();

        // Mocking the loginService
        when(loginService.login(ArgumentMatchers.any(Login.class))).thenReturn(jwtLoginResponse);

        //Setting yp the controller for testing
//        mockMvc = MockMvcBuilders.standaloneSetup(loginController).build();

        // When/Then
        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn();
    }


}