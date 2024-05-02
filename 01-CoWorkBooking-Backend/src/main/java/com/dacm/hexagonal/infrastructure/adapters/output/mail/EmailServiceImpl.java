package com.dacm.hexagonal.infrastructure.adapters.output.mail;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService{

    private JavaMailSender sendMessage;
    private SimpleMailMessage message;

    public EmailServiceImpl(JavaMailSender sendMessage, SimpleMailMessage message) {
        this.sendMessage = sendMessage;
        this.message = message;
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String newPassword) {
        message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom("@example.com");
        message.setSubject("Password reset");
        message.setText("Your new passwsford is: " + newPassword + "\n" +
                "Don't reply this message");

        sendMessage.send(message);
    }

    @Override
    public void sendEmail(String toEmail, String subject, String text) {
        message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom("@example.com");
        message.setSubject(subject);
        message.setText(text);
        sendMessage.send(message);
    }

}
