package com.dacm.hexagonal.domain.model.dto;

import java.time.LocalDateTime;

public record UserBookingDto(
        String userId,
        String spaceId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
