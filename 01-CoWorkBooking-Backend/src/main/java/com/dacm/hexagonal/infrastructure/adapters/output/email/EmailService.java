package com.dacm.hexagonal.infrastructure.adapters.output.email;

import jakarta.mail.MessagingException;
import org.springframework.ui.Model;


public interface EmailService {

    void sendHtmlMessage(String to, String subject, Model model, String templateName) throws MessagingException;

}
