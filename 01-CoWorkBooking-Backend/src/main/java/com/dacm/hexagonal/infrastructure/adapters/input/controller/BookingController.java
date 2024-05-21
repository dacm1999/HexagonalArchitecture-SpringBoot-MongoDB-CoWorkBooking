package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.BookingPaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {


    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Create a new booking.
     *
     * @param bookingDto the booking data transfer object containing the booking details
     * @return a ResponseEntity with the created booking
     */
    @Operation(summary = "Create a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Message.BOOKING_CREATED_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = Message.BOOKING_ERROR)
    })
    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody UserBookingDto bookingDto) throws MessagingException {
        return ResponseEntity.ok(bookingService.saveBooking(bookingDto));
    }

    /**
     * Create multiple bookings at once.
     *
     * @param bookingDtos an array of UserBookingDto each containing booking details
     * @return a ResponseEntity with the created bookings
     */
    @Operation(summary = "Create multiple bookings")
    @PostMapping("/createMultiple")
    public ResponseEntity<?> createMultipleBookings(@RequestBody UserBookingDto[] bookingDtos) throws MessagingException {
        return ResponseEntity.ok(bookingService.saveMultipleBookings(bookingDtos));
    }

    /**
     * Confirm a booking.
     *
     * @param bookingId the ID of the booking to confirm
     * @return a ResponseEntity with the confirmation status
     */
    @Operation(summary = "Confirm a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Message.BOOKING_CONFIRMED_SUCCESSFULLY),
            @ApiResponse(responseCode = "404", description = Message.BOOKING_NOT_FOUND)
    })
    @PutMapping("/confirm/{bookingId}")
    public ResponseEntity<?> confirmBooking(@PathVariable String bookingId) throws MessagingException {
        return ResponseEntity.ok(bookingService.confirmBooking(bookingId));
    }

    /**
     * Cancel a booking.
     *
     * @param bookingId the ID of the booking to cancel
     * @return a ResponseEntity with the cancellation status
     */
    @Operation(summary = "Cancel a booking")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Message.BOOKING_CANCELLED_SUCCESSFULLY),
            @ApiResponse(responseCode = "404", description = Message.BOOKING_NOT_FOUND)
    })
    @PutMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable String bookingId) throws MessagingException {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId));
    }

    /**
     * Retrieve all bookings with pagination.
     *
     * @param page the page number of the pagination
     * @param size the size of the page
     * @return a ResponseEntity containing a BookingPaginationResponse
     */
    @Operation(summary = "Retrieve all bookings with pagination")
    @ApiResponse(responseCode = "200", description = Message.BOOKING_RETREIVED_SUCCESSFULLY)
    @GetMapping("/all")
    public ResponseEntity<?> getAllBookings(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        BookingPaginationResponse response = bookingService.getAllBookings(pageable);
        return ResponseEntity.ok(response);
    }

    /**
     * Retrieve all bookings by start date.
     *
     * @param startDate the start date for which to retrieve bookings
     * @return a ResponseEntity with bookings starting on the given date
     */
    @Operation(summary = "Retrieve all bookings by start date")
    @ApiResponse(responseCode = "200", description = Message.BOOKING_RETREIVED_SUCCESSFULLY)
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
    @Operation(summary = "Get available booking hours for a given date and space")
    @ApiResponse(responseCode = "200", description = Message.AVAILABLE_HOURS)
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
    @Operation(summary = "Retrieve bookings by status")
    @ApiResponse(responseCode = "200", description = Message.BOOKING_RETREIVED_SUCCESSFULLY)
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
    @Operation(summary = "Retrieve bookings by user ID")
    @ApiResponse(responseCode = "200", description = Message.BOOKING_RETREIVED_SUCCESSFULLY)
    @GetMapping("/user/{userId}")
    public ResponseEntity<?> getBookingByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(bookingService.getBookingByUserId(userId));
    }

    /**
     * Retrieve a booking by its ID.
     *
     * @param bookingId the ID of the booking to retrieve
     * @return a ResponseEntity with the booking
     */
    @Operation(summary = "Retrieve a booking by id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Message.BOOKING_RETREIVED_SUCCESSFULLY),
            @ApiResponse(responseCode = "404", description = Message.BOOKING_NOT_FOUND)
    })
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
    @Operation(summary = "Update a booking by id")
    @ApiResponse(responseCode = "200", description = Message.BOOKING_UPDATE_SUCCESSFULLY)
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
    @Operation(summary = "Delete a booking")
    @ApiResponse(responseCode = "200", description = Message.BOOKING_DELETE_SUCCESSFULLY)
    @DeleteMapping("/delete/{bookingId}")
    public ResponseEntity<?> deleteBooking(@PathVariable String bookingId) {
        return ResponseEntity.ok(bookingService.deleteBooking(bookingId));
    }

}
