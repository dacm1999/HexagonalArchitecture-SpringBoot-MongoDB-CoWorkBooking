package com.dacm.hexagonal.application.port.in;

import org.springframework.ui.Model;

public interface KafkaProducerService {

    void sendHtmlMessage(String to, String subject, Model model, String templateName);
}
