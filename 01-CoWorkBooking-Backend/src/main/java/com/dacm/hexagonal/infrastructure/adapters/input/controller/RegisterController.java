package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.RegisterService;
import com.dacm.hexagonal.domain.model.dto.RegisterDto;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.dacm.hexagonal.application.service.RegisterServiceImpl;
import com.dacm.hexagonal.infrastructure.adapters.input.response.JwtLoginResponse;
import jakarta.mail.MessagingException;
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

    private final RegisterService registerService;

    /**
     * Registers a new user in the system.
     * This endpoint accepts a POST request with a body containing the user's registration details encapsulated in a RegisterDto.
     * It handles the registration process through the `registerService`, which performs necessary operations like user validation,
     * password hashing, and persistence of user data. Upon successful registration, it returns a JwtLoginResponse object
     * containing details such as the JWT token, indicating successful authentication of the newly registered user.
     *
     * @param request A RegisterDto object that contains all necessary user registration information such as username, password, email, etc.
     * @return ResponseEntity containing a JwtLoginResponse object with the JWT token on successful registration.
     */
    @PostMapping
    public ResponseEntity<JwtLoginResponse> register(@RequestBody RegisterDto request) throws MessagingException {
        return ResponseEntity.ok(registerService.signUp(request));
    }
}
