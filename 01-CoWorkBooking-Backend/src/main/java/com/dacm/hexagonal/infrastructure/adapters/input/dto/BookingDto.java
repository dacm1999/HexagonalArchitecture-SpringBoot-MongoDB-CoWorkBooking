package com.dacm.hexagonal.infrastructure.adapters.input.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class BookingDto {

    private String spaceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean active;
}
