package com.dacm.hexagonal.infrastructure.web.controller;

import com.dacm.hexagonal.application.service.LoginServiceImpl;
import com.dacm.hexagonal.common.ApiResponse;
import com.dacm.hexagonal.domain.model.Login;
import com.dacm.hexagonal.infrastructure.web.security.jwt.JwtResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<JwtResponse> login(@RequestBody Login request) {
        return ResponseEntity.ok(loginService.login(request));
    }

}
