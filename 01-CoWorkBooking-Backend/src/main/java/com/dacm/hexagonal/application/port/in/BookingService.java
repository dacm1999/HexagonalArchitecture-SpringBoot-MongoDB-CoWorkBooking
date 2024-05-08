package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.domain.model.Booking;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.BookingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.time.LocalDate;
import java.util.List;

public interface BookingService {

    ApiResponse saveBooking(UserBookingDto bookingRecord);
    AddedResponse saveMultipleBookings(UserBookingDto[] bookingDtos);
    Booking confirmBooking(String bookingId);
    Booking cancelBooking(String bookingId);
    Booking updateBooking(String bookingId, BookingDto bookingDto);
    Page<BookingDto> getAllBookings(Pageable pageable);
    Booking getBookingById(String bookingId);
    List<BookingDto> getBookingByUserId(String userId);
    List<BookingDto> getBookingsByStatus(String status);
    ApiResponse deleteBooking(String bookingId);
    List<BookingDto> getBookingsByStartTime(String startDate);
}
