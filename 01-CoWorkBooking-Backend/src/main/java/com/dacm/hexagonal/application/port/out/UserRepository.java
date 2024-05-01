package com.dacm.hexagonal.application.port.out;

import com.dacm.hexagonal.infrastructure.persistence.entity.UserEntity;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface UserRepository extends MongoRepository<UserEntity, String> {

    void deleteByUsername(String username);
    boolean existsByEmail(String email);
    UserEntity findByUsername(String username);
    boolean existsByUsername(String username);

}
