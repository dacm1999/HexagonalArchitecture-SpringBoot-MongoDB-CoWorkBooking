package com.dacm.hexagonal.domain.model.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    public String spaceId;
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    public boolean active;

}
