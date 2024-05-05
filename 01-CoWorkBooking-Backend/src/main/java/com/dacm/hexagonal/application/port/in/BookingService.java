package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.domain.model.Booking;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;


import java.util.List;

public interface BookingService {

    ApiResponse saveBooking(UserBookingDto bookingRecord);
    List<Booking> getAllBookings();
    List<BookingDto> getBookingByUserId(String userId);
}
