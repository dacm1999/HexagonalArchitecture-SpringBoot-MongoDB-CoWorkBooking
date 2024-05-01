package com.dacm.hexagonal.infrastructure.web.response;

import com.dacm.hexagonal.infrastructure.web.dto.BookingRecord;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class BookingPaginationResponse {

//    List<BookingRecord> bookings;
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private int size;
    private int number;
}
