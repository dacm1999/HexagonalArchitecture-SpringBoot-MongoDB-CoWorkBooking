package com.dacm.hexagonal.infrastructure.adapters.input.mapper;

import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.domain.model.dto.SpaceRecord;

public class SpaceMapper {

    private SpaceRecord spaceRecord;

    public static SpaceRecord toDto(SpaceEntity space) {
        return new SpaceRecord(
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
