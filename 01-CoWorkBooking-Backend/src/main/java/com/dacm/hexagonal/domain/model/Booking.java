package com.dacm.hexagonal.domain.model;

import com.dacm.hexagonal.domain.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class Booking {

    private String id;
    private String userId;
    private String spaceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private BookingStatus status;
    private boolean active;

}
