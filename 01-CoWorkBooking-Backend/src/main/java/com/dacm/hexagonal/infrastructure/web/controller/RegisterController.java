package com.dacm.hexagonal.infrastructure.web.controller;

import com.dacm.hexagonal.application.port.out.UserRepository;
import com.dacm.hexagonal.application.service.RegisterServiceImpl;
import com.dacm.hexagonal.infrastructure.web.dto.RegisterDto;
import com.dacm.hexagonal.infrastructure.web.response.JwtLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/register")
@RequiredArgsConstructor
public class RegisterController {

    private final RegisterServiceImpl registerService;
    private final UserRepository userRepository;

    @PostMapping
    public ResponseEntity<JwtLoginResponse> register(@RequestBody RegisterDto request) {
        return ResponseEntity.ok(registerService.signUp(request));
    }
}
