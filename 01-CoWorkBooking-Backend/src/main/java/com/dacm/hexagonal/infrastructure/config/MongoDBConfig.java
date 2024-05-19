package com.dacm.hexagonal.infrastructure.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository")
public class MongoDBConfig extends AbstractMongoClientConfiguration {

    @Value("${v.mongodb.uri}")
    private String mongoUri;

    @Override
    @NonNull
    protected String getDatabaseName() {
        System.out.println(mongoUri);
        return new ConnectionString(mongoUri).getDatabase();
    }

    @Override
    @NonNull
    public MongoClient mongoClient() {
        MongoClientSettings mongoClientSettings = MongoClientSettings.builder()
                .applyConnectionString(new ConnectionString(mongoUri))
                .build();
        return MongoClients.create(mongoClientSettings);
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }
}