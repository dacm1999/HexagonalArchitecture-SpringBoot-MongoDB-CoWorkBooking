package com.dacm.hexagonal.infrastructure.adapters.input.mapper;

import com.dacm.hexagonal.domain.model.Space;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.domain.model.dto.SpaceDto;
import org.springframework.stereotype.Component;

@Component
public class SpaceMapper {

    public static SpaceDto entityDto(SpaceEntity space) {
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

    public static SpaceDto modelToDto(Space space) {
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

    public static SpaceDto toDto(SpaceEntity space) {
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
}
