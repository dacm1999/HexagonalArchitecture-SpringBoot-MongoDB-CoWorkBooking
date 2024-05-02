package com.dacm.hexagonal.infrastructure.adapters.input.dto;

import java.time.LocalDateTime;

public record UserBookingRecord(
        String username,
        String spaceId,
        LocalDateTime startTime,
        LocalDateTime endTime
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy hh:mm:ss")
//        LocalDateTime startTime,
//        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-mm-yyyy hh:mm:ss")
//        LocalDateTime endTime
) {
}
