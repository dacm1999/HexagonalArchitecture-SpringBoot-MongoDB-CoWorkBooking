package com.dacm.hexagonal.infrastructure.web.dto;

import java.util.List;

public record SpaceRecord(
        String spaceId,
        String spaceName,
        String description,
        int capacity,
        List<String> amenities,
        boolean available,
        String location
) {
}
