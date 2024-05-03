package com.dacm.hexagonal.domain.model.dto;

import java.time.LocalDateTime;

public record UserBookingDto(
        String username,
        String spaceId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
