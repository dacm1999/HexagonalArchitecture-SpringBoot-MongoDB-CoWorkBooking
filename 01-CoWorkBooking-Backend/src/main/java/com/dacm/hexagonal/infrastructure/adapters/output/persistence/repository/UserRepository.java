package com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository;

import com.dacm.hexagonal.domain.model.User;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface UserRepository extends MongoRepository<UserEntity, String> {

    void deleteByUsername(String username);
    boolean existsByEmail(String email);
    UserEntity findByUsername(String username);
    boolean existsByUsername(String username);
}
