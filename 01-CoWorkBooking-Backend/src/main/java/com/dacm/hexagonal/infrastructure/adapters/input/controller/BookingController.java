package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.infrastructure.adapters.input.dto.UserBookingRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {


    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody UserBookingRecord bookingRecord) {
        return ResponseEntity.ok(bookingService.saveBooking(bookingRecord));
    }
}
