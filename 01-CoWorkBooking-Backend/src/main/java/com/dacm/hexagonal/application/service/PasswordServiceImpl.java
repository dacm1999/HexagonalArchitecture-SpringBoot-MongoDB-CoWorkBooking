package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.KafkaProducerService;
import com.dacm.hexagonal.application.port.in.PasswordService;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.domain.model.dto.PasswordDto;
import com.dacm.hexagonal.domain.model.dto.PasswordResetDto;
import com.dacm.hexagonal.infrastructure.adapters.input.mapper.UserMapper;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.adapters.output.email.EmailService;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PasswordServiceImpl implements PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final KafkaProducerService kafkaProducerService;
    private static final String ALLOWED_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_-+=<>?";


    @Override
    public ApiResponse resetPassword(PasswordResetDto request) throws MessagingException {
        UserEntity user = userRepository.findByEmail(request.getEmail());
        if (user == null) {
            throw new IllegalArgumentException(Message.EMAIL_NOT_FOUND);
        }

        int passwordLength = 15;
        String newPassword = passwordEncoder.encode(generateRandomPassword(passwordLength));

        user.setPassword(newPassword);
        userRepository.save(user);

        String fullName = user.getFirstName() + " " + user.getLastName();

        Model model = new ExtendedModelMap();
        model.addAttribute("fullName", fullName);
        model.addAttribute("newPassword", newPassword);

        kafkaProducerService.sendHtmlMessage(request.getEmail(), "Password Reset", model, "password-reset");
        return new ApiResponse(
                HttpStatus.OK.value(),
                Message.PASSWORD_RESET_EMAIL_SENT,
                HttpStatus.OK,
                LocalDateTime.now(),
                UserMapper.entityToDto(user)
        );
    }

    @Override
    public ApiResponse updatePassword(String userId, PasswordDto request) throws MessagingException {
        UserEntity user = userRepository.findByUserId(userId);

        if (user == null) {
            throw new IllegalArgumentException(Message.USER_NOT_FOUND);
        }

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException(Message.INVALID_PASSWORD);
        }

        String encodedPassword = passwordEncoder.encode(request.getNewPassword());

        user.setPassword(encodedPassword);
        userRepository.save(user);
        String fullName = user.getFirstName() + " " + user.getLastName();

        Model model = new ExtendedModelMap();
        model.addAttribute("fullName", fullName);

        kafkaProducerService.sendHtmlMessage(user.getEmail(), "Password Update", model, "password-updated");
        return new ApiResponse(200, Message.PASSWORD_UPDATED, HttpStatus.OK, LocalDateTime.now(), "");
    }

    @Override
    public String generateRandomPassword(int length) {
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(ALLOWED_CHARACTERS.length());
            password.append(ALLOWED_CHARACTERS.charAt(randomIndex));
        }
        return password.toString();
    }
}
