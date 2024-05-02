package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.infrastructure.adapters.input.dto.UserBookingRecord;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;

public interface BookingService {

    ApiResponse saveBooking(UserBookingRecord bookingRecord);
//    UserBookingPaginationResponse getUserBookings(String username, Pageable pageable);

}
