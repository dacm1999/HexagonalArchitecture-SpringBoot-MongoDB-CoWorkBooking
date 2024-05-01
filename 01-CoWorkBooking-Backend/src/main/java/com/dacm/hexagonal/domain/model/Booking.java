package com.dacm.hexagonal.domain.model;

import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.persistence.entity.UserEntity;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Booking {

    private String id;
    private UserEntity user;
    private SpaceEntity space;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}
