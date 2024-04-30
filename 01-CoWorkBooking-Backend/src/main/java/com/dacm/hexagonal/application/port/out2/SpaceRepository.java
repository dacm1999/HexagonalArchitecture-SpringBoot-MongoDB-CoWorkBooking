package com.dacm.hexagonal.application.port.out2;

import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface SpaceRepository extends MongoRepository<SpaceEntity, String> {

    void deleteBySpaceName(String spaceName);
    boolean existsBySpaceName(String spaceName);
    Optional<SpaceEntity> findBySpaceName(String spaceName);

}
