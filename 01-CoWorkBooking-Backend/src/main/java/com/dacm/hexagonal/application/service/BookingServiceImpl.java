package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.application.port.out2.BookingRepository;
import com.dacm.hexagonal.application.port.out2.SpaceRepository;
import com.dacm.hexagonal.application.port.out2.UserRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.infrastructure.persistence.entity.BookingEntity;
import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.web.dto.BookingRecord;
import com.dacm.hexagonal.infrastructure.web.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

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
        // Verify if the user exists
        UserEntity user = userRepository.findByUsername(bookingRecord.username());
        if (user == null) {
            throw new UsernameNotFoundException(Message.USER_NOT_FOUND);
        }

        // Verify if the space exists
        SpaceEntity space = spaceRepository.findBySpaceId(bookingRecord.spaceId()).
                orElseThrow(() -> new RuntimeException(Message.SPACE_NOT_FOUND));

        // Verify if the space is avaliable
        if (!space.isAvailable()) {
            return new ApiResponse(400, Message.SPACE_NOT_AVAILABLE, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        // change availability of the space
        spaceService.changeSpaceAvailability(space, false);

        // Create the booking
        BookingEntity booking = BookingEntity.builder()
                .user(user)
                .space(space)
                .startTime(LocalDateTime.parse(bookingRecord.startTime()))
                .endTime(LocalDateTime.parse(bookingRecord.endTime()))
                .build();

        // Save the booking
        bookingRepository.save(booking);

        return new ApiResponse(200, Message.BOOKING_CREATED_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());
    }

}
