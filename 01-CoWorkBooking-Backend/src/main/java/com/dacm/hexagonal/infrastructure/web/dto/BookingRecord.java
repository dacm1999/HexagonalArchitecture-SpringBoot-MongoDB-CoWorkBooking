package com.dacm.hexagonal.infrastructure.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record BookingRecord(
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
