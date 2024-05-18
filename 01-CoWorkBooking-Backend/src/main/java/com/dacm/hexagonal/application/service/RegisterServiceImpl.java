package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.RegisterService;
import com.dacm.hexagonal.infrastructure.adapters.output.email.EmailService;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.common.CommonMethods;
import com.dacm.hexagonal.domain.enums.UserRole;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.domain.model.dto.RegisterDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.JwtLoginResponse;
import com.dacm.hexagonal.infrastructure.security.jwt.JwtTokenProvider;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

@Service
public class RegisterServiceImpl implements RegisterService {

    private JwtTokenProvider jwtTokenProvider;

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final EmailService emailService;

    @Autowired
    public RegisterServiceImpl(JwtTokenProvider jwtTokenProvider, UserRepository userRepository, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public JwtLoginResponse signUp(RegisterDto request) throws MessagingException {
        Model model = new ExtendedModelMap();
        String fullName = request.getFirstname() + " " + request.getLastname();

        if (CommonMethods.isEmpty(request.getUserId())) {
            throw new IllegalArgumentException(Message.USERNAME_MANDATORY);
        } else if (userRepository.existsByUserId(request.getUserId())) {
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
                .userId(request.getUserId())
                .password(passwordEncoder.encode(request.getPassword()))
                .firstName(request.getFirstname())
                .lastName(request.getLastname())
                .email(request.getEmail())
                .role(UserRole.ROLE_ADMIN)
                .build();

        userRepository.save(user);
        model.addAttribute("nombre", fullName);
        emailService.sendHtmlMessage(user.getEmail(), "Bienvenido a Nuestro Servicio", model, "successful-registration.html");

        return JwtLoginResponse.builder()
                .token(jwtTokenProvider.getToken(user))
                .build();
    }

}
