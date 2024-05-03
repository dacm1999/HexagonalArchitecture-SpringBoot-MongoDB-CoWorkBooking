package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.UserBookingPaginationResponse;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.BookingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {


    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody UserBookingDto bookingRecord) {
        return ResponseEntity.ok(bookingService.saveBooking(bookingRecord));
    }

    @GetMapping("/user/{username}")
    public ResponseEntity<?> getBookingsByUser(
            @PathVariable String username,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingDto> bookings = bookingService.getBookingsByUser(username, pageable);

        UserBookingPaginationResponse response = UserBookingPaginationResponse.builder()
                .username(username)
                .bookings(bookings.getContent())
                .totalElements(bookings.getTotalElements())
                .totalPages(bookings.getTotalPages())
                .numberOfElements(bookings.getNumberOfElements())
                .size(bookings.getSize())
                .number(bookings.getNumber())
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/space/{spaceId}")
    public ResponseEntity<?> getSpacesBySpaceId(
            @PathVariable String spaceId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<BookingEntity> spaces = bookingService.getSpacesBySpaceId(spaceId, pageable);


        return ResponseEntity.ok(spaces);
    }
}
