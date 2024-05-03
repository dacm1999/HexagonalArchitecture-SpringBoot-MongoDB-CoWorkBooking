package com.dacm.hexagonal.domain.model.dto;

import java.time.LocalDateTime;

public record UserBookingRecord(
        String username,
        String spaceId,
        LocalDateTime startTime,
        LocalDateTime endTime
) {
}
