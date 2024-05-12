package com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository;

import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<UserEntity, String> {

    void deleteByUserId(String username);
    boolean existsByEmail(String email);
    UserEntity findByEmail(String email);
    UserEntity findByUserId(String userId);
    boolean existsByUserId(String userId);
}
