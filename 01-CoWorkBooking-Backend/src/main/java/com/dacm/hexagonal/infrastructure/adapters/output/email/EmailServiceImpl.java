package com.dacm.hexagonal.infrastructure.adapters.output.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Override
    public void sendPasswordResetEmail(String toEmail, String newPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setFrom("@example.com");
        message.setSubject("Password reset");
        message.setText("Your new password is: " + newPassword + "\nDon't reply this message");

        mailSender.send(message);
    }

    @Override
    public void sendHtmlMessage(String to, String subject, Model model, String templateName) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        Context context = new Context();
        model.asMap().forEach(context::setVariable);
        String html = templateEngine.process(templateName, context);

        helper.setTo(to);
        helper.setFrom("tasksync@hotmail.com");  // Asegúrate de que esta dirección está autorizada
        helper.setText(html, true);
        helper.setSubject(subject);

        try {
            mailSender.send(message);
        } catch (MailException e) {
            // Log the exception, handle it, or throw it
            throw new MessagingException("Failed to send email", e);
        }
    }
}
