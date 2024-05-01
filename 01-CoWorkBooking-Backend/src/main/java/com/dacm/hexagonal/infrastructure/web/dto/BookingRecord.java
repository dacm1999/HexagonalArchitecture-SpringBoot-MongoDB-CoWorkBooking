package com.dacm.hexagonal.infrastructure.web.dto;

public record BookingRecord(
        String id,
        String username,
        String spaceId,
        String startTime,
        String endTime
) {
}
