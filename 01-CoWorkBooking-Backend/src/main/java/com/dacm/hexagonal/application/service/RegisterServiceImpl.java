package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.RegisterService;
import com.dacm.hexagonal.application.port.out.UserRepository;
import com.dacm.hexagonal.domain.enums.Role;
import com.dacm.hexagonal.infrastructure.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.web.dto.RegisterDto;
import com.dacm.hexagonal.infrastructure.web.security.jwt.JwtResponse;
import com.dacm.hexagonal.infrastructure.web.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    private final UserRepository userRepository;

    private final JwtTokenProvider jwtTokenProvider;

    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;


    @Override
    public JwtResponse signUp(RegisterDto request) {
        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .role(Role.ROLE_ADMIN)
                .build();

        userRepository.save(user);

        return JwtResponse.builder()
                .token(jwtTokenProvider.getToken(user))
                .build();
    }

}
