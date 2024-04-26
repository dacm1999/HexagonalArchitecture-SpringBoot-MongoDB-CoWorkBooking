package com.dacm.hexagonal.infrastructure.web.controller;

import com.dacm.hexagonal.application.port.out.UserRepository;
import com.dacm.hexagonal.application.service.RegisterServiceImpl;
import com.dacm.hexagonal.infrastructure.web.dto.RegisterDto;
import com.dacm.hexagonal.infrastructure.web.security.jwt.JwtResponse;
import io.micrometer.common.util.StringUtils;
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
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterDto request) {

        if(StringUtils.isEmpty(request.getUsername())){
            throw new IllegalArgumentException("Username must be mandatory");
        } else if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered.");
        }

        if(StringUtils.isEmpty(request.getEmail())){
            throw new IllegalArgumentException("Email must be mandatory");

        } else if(userRepository.existsByUsernameContainsIgnoreCase(request.getUsername())){
            throw new IllegalArgumentException("Username already registered.");
        }

        if (StringUtils.isEmpty(request.getPassword())) {
            throw new IllegalArgumentException("Password must be mandatory.");
        } else if (request.getPassword().length() < 5) {
            throw new IllegalArgumentException("The password must be more than 5 characters.");
        }

        return ResponseEntity.ok(registerService.signUp(request));

    }
}
