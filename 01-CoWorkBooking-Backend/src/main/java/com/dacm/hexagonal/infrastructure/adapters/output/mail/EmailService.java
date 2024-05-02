package com.dacm.hexagonal.infrastructure.adapters.output.mail;

import org.springframework.mail.SimpleMailMessage;


public interface EmailService {

    public void sendPasswordResetEmail(String toEmail, String newPassword);

    public void sendEmail(String toEmail, String subject, String text);
}
