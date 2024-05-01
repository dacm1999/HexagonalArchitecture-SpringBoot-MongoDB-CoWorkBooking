package com.dacm.hexagonal.infrastructure.web.controller;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.infrastructure.web.dto.BookingRecord;
import com.dacm.hexagonal.infrastructure.web.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {


    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRecord bookingRecord) {
        return ResponseEntity.ok(bookingService.saveBooking(bookingRecord));
    }
}
