package com.dacm.hexagonal.domain.model.dto;

import com.dacm.hexagonal.domain.enums.BookingStatus;
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
    private BookingStatus status;
    public boolean active;

}
