package com.dacm.hexagonal.infrastructure.adapters.input.response;

import com.dacm.hexagonal.domain.model.dto.BookingDto;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserBookingPaginationResponse {

    private String username;
    List<BookingDto> bookings;
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private int size;
    private int number;

}

