package com.dacm.hexagonal.domain.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Booking {

    private String id;
    private String userId;
    private String spaceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
