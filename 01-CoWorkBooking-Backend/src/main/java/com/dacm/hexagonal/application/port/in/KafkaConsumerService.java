package com.dacm.hexagonal.application.port.in;

public interface KafkaConsumerService {

    public void listenEmailTopic(String message);
}
