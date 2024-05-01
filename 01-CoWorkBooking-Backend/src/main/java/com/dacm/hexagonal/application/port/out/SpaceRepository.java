package com.dacm.hexagonal.application.port.out;

import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface SpaceRepository extends MongoRepository<SpaceEntity, String> {

    void deleteBySpaceName(String spaceName);
    List<SpaceEntity> findByAvailable();
    Optional<SpaceEntity> findBySpaceName(String spaceName);
    Optional<SpaceEntity> findBySpaceId(String spaceId);
}
