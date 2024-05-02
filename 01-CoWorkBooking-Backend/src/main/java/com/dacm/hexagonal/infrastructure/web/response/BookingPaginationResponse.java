package com.dacm.hexagonal.infrastructure.web.response;

import com.dacm.hexagonal.infrastructure.web.dto.BookingRecord;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
public class BookingPaginationResponse {

    private String username;
    List<BookingRecord> bookings;
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private int size;
    private int number;
}
