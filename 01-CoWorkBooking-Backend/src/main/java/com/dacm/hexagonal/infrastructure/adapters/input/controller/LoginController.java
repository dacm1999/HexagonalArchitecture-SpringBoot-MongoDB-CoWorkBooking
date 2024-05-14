package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.LoginService;
import com.dacm.hexagonal.application.service.LoginServiceImpl;
import com.dacm.hexagonal.domain.model.Login;
import com.dacm.hexagonal.infrastructure.adapters.input.response.JwtLoginResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    /**
     * Authenticate a user and return a JWT token.
     * This endpoint receives a JSON object representing login credentials (username and password),
     * and if the authentication is successful, it returns a JWT token encapsulated in a JwtLoginResponse object.
     *
     * @param request A Login object containing the user's authentication credentials.
     * @return ResponseEntity containing a JwtLoginResponse object which includes the JWT token on successful authentication.
     */
    @PostMapping("/auth")
    public ResponseEntity<JwtLoginResponse> login(@RequestBody Login request) {
        return ResponseEntity.ok(loginService.login(request));
    }

}
