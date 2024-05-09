package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.domain.model.Booking;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface BookingService {

    ApiResponse saveBooking(UserBookingDto bookingRecord);

    AddedResponse saveMultipleBookings(UserBookingDto[] bookingDtos);

    Booking confirmBooking(String bookingId);

    Booking cancelBooking(String bookingId);

    Booking updateBooking(String bookingId, BookingDto bookingDto);

    Booking getBookingById(String bookingId);

    Page<BookingDto> getAllBookings(Pageable pageable);

    List<BookingDto> getBookingByUserId(String userId);

    List<BookingDto> getBookingsByStatus(String status);

    List<BookingDto> getBookingsByDate(String date);

    ApiResponse getAvailableHoursByDateAndSpace(String spaceId, String date);

    ApiResponse deleteBooking(String bookingId);
}
