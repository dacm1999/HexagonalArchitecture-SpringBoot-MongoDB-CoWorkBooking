package com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository;

import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

/**
 * Space Repository interface for handling database operations related to Space entities.
 * This repository interface provides CRUD operations inherited from MongoRepository
 * and includes custom query methods for spaces.
 */
public interface SpaceRepository extends MongoRepository<SpaceEntity, String> {

    Optional<SpaceEntity> findBySpaceId(String spaceId);


}
