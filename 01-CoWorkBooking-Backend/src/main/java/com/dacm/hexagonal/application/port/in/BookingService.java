package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.domain.model.Booking;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.BookingPaginationResponse;
import jakarta.mail.MessagingException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.util.List;

public interface BookingService {

    ApiResponse saveBooking(UserBookingDto bookingRecord) throws MessagingException;

    AddedResponse saveMultipleBookings(UserBookingDto[] bookingDtos) throws MessagingException;

    Booking confirmBooking(String bookingId) throws MessagingException;

    Booking cancelBooking(String bookingId) throws MessagingException;

    Booking updateBooking(String bookingId, BookingDto bookingDto);

    Booking getBookingById(String bookingId);

    Page<BookingDto> getAllBookingsDto(Pageable pageable);

    BookingPaginationResponse getAllBookings(Pageable pageable);

    List<BookingDto> getBookingByUserId(String userId);

    List<BookingDto> getBookingsByStatus(String status);

    List<BookingDto> getBookingsByDate(String date);

    ApiResponse getAvailableHoursByDateAndSpace(String spaceId, String date);

    ApiResponse deleteBooking(String bookingId);
}
