package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.KafkaConsumerService;
import com.dacm.hexagonal.domain.model.dto.EmailPayload;
import com.dacm.hexagonal.infrastructure.adapters.output.email.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.util.Map;

@Service
public class KafkaConsumerServiceImpl implements KafkaConsumerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerServiceImpl.class);
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    public KafkaConsumerServiceImpl(EmailService emailService, ObjectMapper objectMapper) {
        this.emailService = emailService;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "${v.kafka.topic.email}", groupId = "${v.kafka.consumer.group.id}")
    public void listenEmailTopic(String message) {
        logger.info("Received message: {}", message);
        System.out.println("Received message: " + message);
        try {
            EmailPayload payload = objectMapper.readValue(message, EmailPayload.class);
            Model model = new ExtendedModelMap();
            payload.getModel().forEach(model::addAttribute);

            emailService.sendHtmlMessage(payload.getTo(), payload.getSubject(), model, payload.getTemplateName());
            logger.info("Email sent successfully to {}", payload.getTo());
        } catch (Exception e) {
            logger.error("Failed to process email message", e);
        }
    }

}

