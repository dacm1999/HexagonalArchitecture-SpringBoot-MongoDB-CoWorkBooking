package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.BookingPaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {


    @Autowired
    private BookingService bookingService;

    /**
     * Create a new booking.
     *
     * @param bookingDto the booking data transfer object containing the booking details
     * @return a ResponseEntity with the created booking
     */
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody UserBookingDto bookingDto) {
        return ResponseEntity.ok(bookingService.saveBooking(bookingDto));
    }

    /**
     * Create multiple bookings at once.
     *
     * @param bookingDtos an array of UserBookingDto each containing booking details
     * @return a ResponseEntity with the created bookings
     */
    @PostMapping("/createMultiple")
    public ResponseEntity<?> createMultipleBookings(@RequestBody UserBookingDto[] bookingDtos) {
        return ResponseEntity.ok(bookingService.saveMultipleBookings(bookingDtos));
    }

    /**
     * Confirm a booking.
     *
     * @param bookingId the ID of the booking to confirm
     * @return a ResponseEntity with the confirmation status
     */
    @PutMapping("/confirm/{bookingId}")
    public ResponseEntity<?> confirmBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.confirmBooking(bookingId));
    }

    /**
     * Cancel a booking.
     *
     * @param bookingId the ID of the booking to cancel
     * @return a ResponseEntity with the cancellation status
     */
    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }

    /**
     * Retrieve all bookings with pagination.
     *
     * @param page the page number of the pagination
     * @param size the size of the page
     * @return a ResponseEntity containing a BookingPaginationResponse
     */
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


    /**
     * Retrieve all bookings by start date.
     *
     * @param startDate the start date for which to retrieve bookings
     * @return a ResponseEntity with bookings starting on the given date
     */
    @GetMapping("allByStartDate/{startDate}")
    public ResponseEntity<?> getAllBookingsByStartDate(
            @PathVariable String startDate) {
        return ResponseEntity.ok(bookingService.getBookingsByDate(startDate));
    }

    /**
     * Get available booking hours for a given date and space.
     *
     * @param spaceId the ID of the space
     * @param date    the date for checking availability
     * @return a ResponseEntity with available booking hours
     */
    @GetMapping("availableHours/{spaceId}/{date}")
    public ResponseEntity<?> getAvailableHoursByDateAndSpace(
            @PathVariable String spaceId,
            @PathVariable String date) {
        return ResponseEntity.ok(bookingService.getAvailableHoursByDateAndSpace(spaceId, date));
    }

    /**
     * Retrieve bookings by status.
     *
     * @param status the status to filter bookings
     * @return a ResponseEntity with bookings having the specified status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<?> getBookingsByStatus(@PathVariable String status) {
        return ResponseEntity.ok(bookingService.getBookingsByStatus(status));
    }

    /**
     * Get a booking by the user ID.
     *
     * @param userId the ID of the user whose booking is to be retrieved
     * @return a ResponseEntity with the booking of the specified user
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingByUserId2(@PathVariable String userId) {
        return ResponseEntity.ok(bookingService.getBookingByUserId(userId));
    }

    /**
     * Retrieve a booking by its ID.
     *
     * @param bookingId the ID of the booking to retrieve
     * @return a ResponseEntity with the booking
     */
    @GetMapping("/find/{bookingId}")
    public ResponseEntity<?> getBookingById(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.getBookingById(bookingId));
    }

    /**
     * Update a booking.
     *
     * @param bookingId the ID of the booking to update
     * @param booking   the updated booking data
     * @return a ResponseEntity with the updated booking
     */
    @PatchMapping("update/{bookingId}")
    public ResponseEntity<?> updateBooking(@PathVariable String bookingId, @RequestBody BookingDto booking) {
        return ResponseEntity.ok(bookingService.updateBooking(bookingId, booking));
    }

    /**
     * Delete a booking.
     *
     * @param bookingId the ID of the booking to delete
     * @return a ResponseEntity indicating success or failure
     */
    @DeleteMapping("/delete/{bookingId}")
    public ResponseEntity<?> deleteBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.deleteBooking(bookingId));
    }


}
