package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.LoginService;
import com.dacm.hexagonal.application.port.out.UserRepository;
import com.dacm.hexagonal.common.ApiResponse;
import com.dacm.hexagonal.domain.model.Login;
import com.dacm.hexagonal.infrastructure.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.web.security.jwt.JwtResponse;
import com.dacm.hexagonal.infrastructure.web.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtResponse login(Login request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        UserEntity user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        String token = jwtTokenProvider.getToken(user);

        //Verify if the token has expired
        return JwtResponse.builder()
                .token(token)
                .build();
    }
}
