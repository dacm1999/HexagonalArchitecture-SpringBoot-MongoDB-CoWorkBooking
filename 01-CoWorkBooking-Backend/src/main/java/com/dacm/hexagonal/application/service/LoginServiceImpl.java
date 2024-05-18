package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.LoginService;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.domain.model.Login;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.adapters.input.response.JwtLoginResponse;
import com.dacm.hexagonal.infrastructure.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtLoginResponse login(Login request) {
        if (!StringUtils.hasText(request.getUserId())) {
            throw new IllegalArgumentException(Message.USERNAME_MANDATORY);
        } else if (!StringUtils.hasText(request.getPassword())) {
            throw new IllegalArgumentException(Message.PASSWORD_MANDATORY);

        }

        UserEntity user = userRepository.findByUserId(request.getUserId());
        if (user == null) {
            throw new IllegalStateException(Message.LOGIN_INVALID_USERNAME);
        }

        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUserId(), request.getPassword()));
        } catch (BadCredentialsException e) {
            throw new IllegalStateException(Message.LOGIN_INVALID_PASSWORD);
        }

        String token = jwtTokenProvider.getToken(user);
        return JwtLoginResponse.builder()
                .token(token)
                .build();
    }
}
