package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.RegisterService;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.common.CommonMethods;
import com.dacm.hexagonal.domain.enums.Role;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.adapters.input.dto.RegisterDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.JwtLoginResponse;
import com.dacm.hexagonal.infrastructure.config.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;


    @Override
    public JwtLoginResponse signUp(RegisterDto request) {

        if (CommonMethods.isEmpty(request.getUsername())) {
            throw new IllegalArgumentException(Message.USERNAME_MANDATORY);
        } else if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalArgumentException(Message.USERNAME_TAKEN);
        }

        if (CommonMethods.isEmpty(request.getEmail())) {
            throw new IllegalArgumentException(Message.EMAIL_MANDATORY);
        } else if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException(Message.EMAIL_TAKEN);
        }

        if (CommonMethods.isEmpty(request.getPassword())) {
            throw new IllegalArgumentException(Message.PASSWORD_MANDATORY);
        } else if (request.getPassword().length() < 5) {
            throw new IllegalArgumentException(Message.PASSWORD_LENGTH);
        }

        UserEntity user = UserEntity.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .role(Role.ROLE_ADMIN)
                .build();

        userRepository.save(user);

        return JwtLoginResponse.builder()
                .token(jwtTokenProvider.getToken(user))
                .build();
    }

}
