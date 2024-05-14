package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.LoginService;
import com.dacm.hexagonal.domain.model.Login;
import com.dacm.hexagonal.infrastructure.adapters.input.response.JwtLoginResponse;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.dacm.hexagonal.infrastructure.config.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class LoginServiceImplTest {

    PasswordEncoder passwordEncoder;

    @Autowired
    UserRepository userRepository;

    @Autowired
    LoginService loginService;

    @Autowired
    JwtTokenProvider jwtTokenProvider;

    @Mock
    AuthenticationManager authenticationManager;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userRepository = mock(UserRepository.class);
        authenticationManager = mock(AuthenticationManager.class);
        passwordEncoder = mock(PasswordEncoder.class);
        loginService = new LoginServiceImpl(userRepository, jwtTokenProvider, authenticationManager);
    }


    @Test
    @DisplayName("Test login with valid credentials")
    public void testLogin_WithValidCredentials() {
        // Mock data
        String userId = "testUser";
        String password = "testPassword";
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setPassword(password);

        // Mock repository behavior
        UserRepository userRepository = mock(UserRepository.class);
        when(userRepository.findByUserId(userId)).thenReturn(user);

        // Mock authentication manager behavior
        AuthenticationManager authenticationManager = mock(AuthenticationManager.class);
        when(authenticationManager.authenticate(any())).thenReturn(null);

        // Mock token provider behavior
        JwtTokenProvider jwtTokenProvider = mock(JwtTokenProvider.class);
        String mockToken = "mockToken";
        when(jwtTokenProvider.getToken(user)).thenReturn(mockToken);

        // Create login service with mocked dependencies
        LoginServiceImpl loginService = new LoginServiceImpl(userRepository, jwtTokenProvider, authenticationManager);

        // Execute the method under test
        JwtLoginResponse response = loginService.login(new Login(userId, password));

        // Verify
        assertEquals(mockToken, response.getToken());
    }


    @Test
    @DisplayName("Test login with invalid username")
    public void testLogin_WithInvalidUsername() {
        // Mock data
        String invalidUserId = "invalidUser";

        // Mock repository behavior
        when(userRepository.findByUserId(invalidUserId)).thenReturn(null);

        // Verify that the method throws IllegalStateException
        assertThrows(IllegalStateException.class, () -> loginService.login(new Login(invalidUserId, "password")));
    }

    @Test
    @DisplayName("Test login with invalid password")
    public void testLogin_WithInvalidPassword() {
        // Mock data
        String userId = "testUser";
        String invalidPassword = "invalidPassword";
        UserEntity user = new UserEntity();
        user.setUserId(userId);
        user.setPassword("correctPassword"); // Password in the mock user is different

        // Mock repository behavior
        when(userRepository.findByUserId(userId)).thenReturn(user);

        // Mock authentication manager behavior to throw BadCredentialsException
        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Invalid credentials"));

        // Verify that the method throws IllegalStateException
        assertThrows(IllegalStateException.class, () -> loginService.login(new Login(userId, invalidPassword)));
    }

    @Test
    @DisplayName("Test login with missing username")
    public void testLogin_WithMissingUsername() {
        // Crear una solicitud sin nombre de usuario
        Login loginRequest = new Login();
        loginRequest.setPassword("testPassword");

        // Verificar que se lance una excepción IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> loginService.login(loginRequest));
    }

    @Test
    @DisplayName("Test login with missing password")
    public void testLogin_WithMissingPassword() {
        // Crear una solicitud sin contraseña
        Login loginRequest = new Login();
        loginRequest.setUserId("testUser");

        // Verificar que se lance una excepción IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> loginService.login(loginRequest));
    }

}