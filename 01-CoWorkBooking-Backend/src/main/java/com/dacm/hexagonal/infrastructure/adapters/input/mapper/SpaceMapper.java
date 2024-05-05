package com.dacm.hexagonal.infrastructure.adapters.input.mapper;

import com.dacm.hexagonal.domain.model.Space;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.domain.model.dto.SpaceDto;
import org.springframework.stereotype.Component;

@Component
public class SpaceMapper {

    /**
     * Converts a SpaceEntity to a SpaceDto.
     * @param space SpaceEntity to convert.
     * @return SpaceDto containing the data from the SpaceEntity.
     */
    public static SpaceDto entityToDto(SpaceEntity space) {
        if (space == null) {
            return null;
        }
        return new SpaceDto(
                space.getSpaceId(),
                space.getSpaceName(),
                space.getDescription(),
                space.getCapacity(),
                space.getAmenities(),
                space.isAvailable(),
                space.getLocation()
        );
    }

    /**
     * Converts a Space domain model to a SpaceDto.
     * @param space Space domain model to convert.
     * @return SpaceDto containing the data from the Space domain model.
     */
    public static SpaceDto modelToDto(Space space) {
        if (space == null) {
            return null;
        }
        return new SpaceDto(
                space.getId(),
                space.getSpaceName(),
                space.getDescription(),
                space.getCapacity(),
                space.getAmenities(),
                space.isAvailable(),
                space.getLocation()
        );
    }

    /**
     * Converts a Space domain model to a SpaceEntity.
     * @param space Space domain model to convert.
     * @return SpaceEntity built from the Space domain model.
     */
    public static SpaceEntity modelToEntity(Space space) {
        if (space == null) {
            return null;
        }
        return SpaceEntity.builder()
                .id(space.getId())
                .spaceId(space.getSpaceId())
                .spaceName(space.getSpaceName())
                .description(space.getDescription())
                .capacity(space.getCapacity())
                .amenities(space.getAmenities())
                .available(space.isAvailable())
                .location(space.getLocation())
                .build();
    }

    /**
     * Converts a SpaceEntity to a Space domain model.
     * @param entity SpaceEntity to convert.
     * @return Space domain model built from the SpaceEntity.
     */
    public static Space toDomain(SpaceEntity entity) {
        if (entity == null) {
            return null;
        }
        return Space.builder()
                .id(entity.getId())
                .spaceName(entity.getSpaceName())
                .description(entity.getDescription())
                .capacity(entity.getCapacity())
                .amenities(entity.getAmenities())
                .available(entity.isAvailable())
                .location(entity.getLocation())
                .build();
    }}
