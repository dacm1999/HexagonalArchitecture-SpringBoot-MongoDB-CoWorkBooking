package com.dacm.hexagonal.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class JacksonConfig {

//    @Bean
//    public ObjectMapper objectMapper() {
//        JavaTimeModule module = new JavaTimeModule();
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-ddHH:mm:ss");
//        module.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(formatter));
//        module.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(formatter));
//        return Jackson2ObjectMapperBuilder.json()
//                .modules(module)
//                .build();
//    }

    @Bean
    public ObjectMapper objectMapper() {
        JavaTimeModule module = new JavaTimeModule();
        return Jackson2ObjectMapperBuilder.json()
                .modules(module)
                .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS) // Desactiva la serialización de fechas como timestamps
                .build();
    }
}