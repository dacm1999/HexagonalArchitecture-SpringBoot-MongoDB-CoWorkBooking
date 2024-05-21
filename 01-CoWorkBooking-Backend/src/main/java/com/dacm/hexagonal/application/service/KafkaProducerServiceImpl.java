package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.KafkaProducerService;
import com.dacm.hexagonal.domain.model.dto.EmailPayload;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.ui.Model;

import java.util.Map;

@Service
public class KafkaProducerServiceImpl implements KafkaProducerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaProducer.class);
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    @Value("${v.kafka.topic.email}")
    private String topicEmail;

    @Autowired
    public KafkaProducerServiceImpl(KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }


    @Override
    public void sendHtmlMessage(String to, String subject, Model model, String templateName) {
        try {
            EmailPayload payload = new EmailPayload(to, subject, model.asMap(), templateName);
            String message = objectMapper.writeValueAsString(payload);
//            logger.info("Sending email message: {}", message);
            kafkaTemplate.send(topicEmail, message);
        } catch (JsonProcessingException e) {
            logger.error("Failed to serialize email payload", e);
        }
    }
}
