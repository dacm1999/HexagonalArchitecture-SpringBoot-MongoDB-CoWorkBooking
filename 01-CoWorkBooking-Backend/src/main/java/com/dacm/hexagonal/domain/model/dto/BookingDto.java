package com.dacm.hexagonal.domain.model.dto;

import com.dacm.hexagonal.domain.enums.Status;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BookingDto {

    public String bookingId;
    public String spaceId;
    public LocalDateTime startTime;
    public LocalDateTime endTime;
    private Status status;
    public boolean active;

}
