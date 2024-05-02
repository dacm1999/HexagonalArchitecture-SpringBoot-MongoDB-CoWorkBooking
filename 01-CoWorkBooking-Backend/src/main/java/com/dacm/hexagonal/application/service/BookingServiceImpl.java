package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.application.port.out.BookingRepository;
import com.dacm.hexagonal.application.port.out.SpaceRepository;
import com.dacm.hexagonal.application.port.out.UserRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.infrastructure.persistence.entity.BookingEntity;
import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.web.dto.BookingRecord;
import com.dacm.hexagonal.infrastructure.web.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingServiceImpl implements BookingService {

    private final UserRepository userRepository;
    private final SpaceRepository spaceRepository;
    private final BookingRepository bookingRepository;
    private final SpaceService spaceService;

    @Autowired
    public BookingServiceImpl(UserRepository userRepository, SpaceRepository spaceRepository, BookingRepository bookingRepository, SpaceService spaceService) {
        this.userRepository = userRepository;
        this.spaceRepository = spaceRepository;
        this.bookingRepository = bookingRepository;
        this.spaceService = spaceService;
    }

    @Override
    public ApiResponse saveBooking(BookingRecord bookingRecord) {
        // Check if user exists
        UserEntity user = userRepository.findByUsername(bookingRecord.username());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Check if space exists
        SpaceEntity space = spaceRepository.findBySpaceId(bookingRecord.spaceId())
                .orElseThrow(() -> new RuntimeException("Space not found"));

        // Check if space is available
        if (!space.isAvailable()) {
            return new ApiResponse(400, Message.SPACE_NOT_AVAILABLE, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        // Check if booking start time is after end time
        if (bookingRecord.startTime().isAfter(bookingRecord.endTime())) {
            return new ApiResponse(400, Message.BOOKING_INVALID_START_TIME, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        // Check if booking start time is before current time
        if (bookingRecord.startTime().isBefore(LocalDateTime.now())) {
            return new ApiResponse(400, Message.BOOKING_INVALID_TIME, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        // Set space as unavailable
        spaceService.changeSpaceAvailability(space, false);

        BookingEntity booking = BookingEntity.builder().
                user(user).
                space(space).
                spaceId(bookingRecord.spaceId()).
                startTime(bookingRecord.startTime()).
                endTime(bookingRecord.endTime()).
                active(true).
                build();
        bookingRepository.save(booking);

        return new ApiResponse(200, Message.BOOKING_CREATED_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());
    }

}
