package com.dacm.hexagonal.domain.model.dto;

import com.dacm.hexagonal.domain.enums.Status;
import lombok.*;

import java.time.LocalDateTime;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserBookingDto {
    String userId;
    String spaceId;
    LocalDateTime startTime;
    LocalDateTime endTime;
}
