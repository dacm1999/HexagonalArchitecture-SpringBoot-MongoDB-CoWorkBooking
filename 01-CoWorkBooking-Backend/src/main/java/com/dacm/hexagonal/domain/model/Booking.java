package com.dacm.hexagonal.domain.model;

import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class Booking {

    private String id;
    private User user;
//    private String userId;
    private Space space;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean active;

}
