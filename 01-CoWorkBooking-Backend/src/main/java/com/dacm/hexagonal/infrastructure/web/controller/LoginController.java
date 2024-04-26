package com.dacm.hexagonal.infrastructure.web.controller;

import com.dacm.hexagonal.application.port.out.UserRepository;
import com.dacm.hexagonal.application.service.LoginServiceImpl;
import com.dacm.hexagonal.application.service.RegisterServiceImpl;
import com.dacm.hexagonal.domain.exception.LoginErrorResponse;
import com.dacm.hexagonal.domain.model.Login;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginServiceImpl loginService;

    @PostMapping("/auth")
    public ResponseEntity<?> login(@RequestBody Login request) {
        try {
            return ResponseEntity.ok(loginService.login(request));
        } catch (BadCredentialsException e) {
            throw new LoginErrorResponse("Invalid username or password", e);
        } catch (Exception e) {
            throw new LoginErrorResponse("An error occurred while processing your request", e);
        }
    }
}
