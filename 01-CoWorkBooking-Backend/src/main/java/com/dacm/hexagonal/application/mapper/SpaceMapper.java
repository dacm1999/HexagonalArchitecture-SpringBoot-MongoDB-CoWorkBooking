package com.dacm.hexagonal.application.mapper;

import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.web.dto.SpaceRecord;

public class SpaceMapper {

    private SpaceRecord spaceRecord;

        public static SpaceRecord toDto(SpaceEntity space){
            return new SpaceRecord(space.getSpaceName(), space.getDescription(), space.getCapacity(), space.getAmenities(), space.isAvailable(), space.getLocation());
        }
}
