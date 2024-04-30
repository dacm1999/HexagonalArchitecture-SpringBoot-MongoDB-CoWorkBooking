package com.dacm.hexagonal.application.port.out2;

import com.dacm.hexagonal.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface UserRepository extends MongoRepository<UserEntity, String> {

    void deleteByUsername(String username);
    boolean existsByEmail(String email);
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);

}
