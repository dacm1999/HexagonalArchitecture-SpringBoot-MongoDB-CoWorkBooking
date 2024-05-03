package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.mapper.BookingMapper;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.BookingRepository;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.SpaceRepository;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.BookingEntity;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
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
    private final BookingMapper bookingMapper;
    private final MongoTemplate mongoTemplate;


    @Autowired
    public BookingServiceImpl(UserRepository userRepository, SpaceRepository spaceRepository, BookingRepository bookingRepository, SpaceService spaceService, BookingMapper bookingMapper, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.spaceRepository = spaceRepository;
        this.bookingRepository = bookingRepository;
        this.spaceService = spaceService;
        this.bookingMapper = bookingMapper;
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public ApiResponse saveBooking(UserBookingDto bookingRecord) {
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

    @Override
    public Page<BookingDto> getBookingsByUser(String username, Pageable pageable) {
        UserEntity user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException(Message.USER_NOT_FOUND);
        }

        return bookingRepository.findByUserId(user.getId(), pageable);
    }

    @Override
    public Page<BookingEntity> getSpacesBySpaceId(String spaceId, Pageable pageable) {
        // Asegurarse de que el espacio existe
        SpaceEntity space = spaceRepository.findBySpaceId(spaceId)
                .orElseThrow(() -> new RuntimeException(Message.SPACE_WITHOUT_BOOKING));

        // Devolver las reservas asociadas a este espacio
        return bookingRepository.findBySpaceId(space.getId(), pageable);
    }


}
