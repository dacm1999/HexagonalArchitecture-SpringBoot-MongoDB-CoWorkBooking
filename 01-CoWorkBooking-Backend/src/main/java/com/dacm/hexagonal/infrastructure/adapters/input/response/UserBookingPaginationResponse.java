package com.dacm.hexagonal.infrastructure.adapters.input.response;

import com.dacm.hexagonal.infrastructure.adapters.input.dto.BookingDto;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserBookingPaginationResponse {

    private String username;
    List<BookingDto> bookings;
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private int size;
    private int number;

}

