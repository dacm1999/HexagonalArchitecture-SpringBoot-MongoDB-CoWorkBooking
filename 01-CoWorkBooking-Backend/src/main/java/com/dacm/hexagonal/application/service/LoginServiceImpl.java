package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.LoginService;
import com.dacm.hexagonal.application.port.out.UserRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.domain.exception.LoginException;
import com.dacm.hexagonal.domain.model.Login;
import com.dacm.hexagonal.infrastructure.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.web.security.jwt.JwtResponse;
import com.dacm.hexagonal.infrastructure.web.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.naming.AuthenticationException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    @Override
    public JwtResponse login(Login request) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
            UserEntity user = userRepository.findByUsername(request.getUsername())
                    .orElseThrow(() -> {
                        return new AuthenticationException("User not found");
                    });

            String token = jwtTokenProvider.getToken(user);
            return JwtResponse.builder()
                    .token(token)
                    .build();
        } catch (BadCredentialsException e) {
            throw new LoginException(Message.LOGIN_FAILED, e.getCause(), HttpStatus.UNAUTHORIZED, LocalDateTime.now());
        } catch (Exception e) {
            throw new LoginException(Message.INTERNAL_SERVER_ERROR, e.getCause(), HttpStatus.INTERNAL_SERVER_ERROR, LocalDateTime.now());
        }
    }
}
