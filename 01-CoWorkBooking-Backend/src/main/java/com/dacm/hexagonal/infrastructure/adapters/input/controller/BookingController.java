package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.BookingPaginationResponse;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.BookingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {


    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody UserBookingDto bookingDto) {
        return ResponseEntity.ok(bookingService.saveBooking(bookingDto));
    }

    @PostMapping("/createMultiple")
    public ResponseEntity<?> createMultipleBookings(@RequestBody UserBookingDto[] bookingDtos) {
        return ResponseEntity.ok(bookingService.saveMultipleBookings(bookingDtos));
    }

    @PutMapping("/confirm/{bookingId}")
    public ResponseEntity<?> confirmBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.confirmBooking(bookingId));
    }

    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }

    @GetMapping("/all")
    public ResponseEntity<BookingPaginationResponse> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<BookingDto> bookings = bookingService.getAllBookings(pageable);

        BookingPaginationResponse response = new BookingPaginationResponse();
        response.setBookings(bookings.getContent());
        response.setTotalPages(bookings.getTotalPages());
        response.setTotalElements(bookings.getTotalElements());
        response.setNumber(bookings.getNumber());
        response.setNumberOfElements(bookings.getNumberOfElements());
        response.setSize(bookings.getSize());
        return ResponseEntity.ok(response);
    }

    @GetMapping("allByStartDate/{startDate}")
    public ResponseEntity<?> getAllBookingsByStartDate(
            @PathVariable String startDate) {
        return ResponseEntity.ok(bookingService.getBookingsByStartTime(startDate));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<?> getBookingsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(bookingService.getBookingsByStatus(status));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingByUserId2(@PathVariable String userId) {
        return ResponseEntity.ok(bookingService.getBookingByUserId(userId));
    }

    @GetMapping("/find/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

    @PatchMapping("update/{bookingId}")
    public ResponseEntity<?> updateBooking(@PathVariable String bookingId, @RequestBody BookingDto booking) {
        return ResponseEntity.ok(bookingService.updateBooking(bookingId, booking));
    }

    @DeleteMapping("/delete/{bookingId}")
    public ResponseEntity<?> deleteBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.deleteBooking(bookingId));
    }


}
