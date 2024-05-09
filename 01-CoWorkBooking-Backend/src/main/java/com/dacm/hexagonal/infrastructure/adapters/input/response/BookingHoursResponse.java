package com.dacm.hexagonal.infrastructure.adapters.input.response;

import lombok.*;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingHoursResponse {

    List<LocalTime> availableHours;
}
