package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.domain.model.Booking;
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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public ApiResponse saveBooking(UserBookingDto userBookingDto) {
        // Check if user exists
        UserEntity user = userRepository.findByUserId(userBookingDto.userId());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Check if space exists
        SpaceEntity space = spaceRepository.findBySpaceId(userBookingDto.spaceId())
                .orElseThrow(() -> new RuntimeException("Space not found"));

        // Check if space is available
        if (!space.isAvailable()) {
            return new ApiResponse(400, Message.SPACE_NOT_AVAILABLE, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        // Check if booking start time is after end time
        if (userBookingDto.startTime().isAfter(userBookingDto.endTime())) {
            return new ApiResponse(400, Message.BOOKING_INVALID_START_TIME, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        // Check if booking start time is before current time
        if (userBookingDto.startTime().isBefore(LocalDateTime.now())) {
            return new ApiResponse(400, Message.BOOKING_INVALID_TIME, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        // Set space as unavailable
        spaceService.changeSpaceAvailability(space, false);

        BookingEntity booking = BookingEntity.builder().
                userId(userBookingDto.userId()).
//                user(user).
                space(space).
                spaceId(userBookingDto.spaceId()).
                startTime(userBookingDto.startTime()).
                endTime(userBookingDto.endTime()).
                active(true).
                build();


        bookingRepository.save(booking);

        return new ApiResponse(200, Message.BOOKING_CREATED_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now(), booking.getId());
    }

    @Override
    public List<Booking> getAllBookings() {
        List<BookingEntity> bookings = bookingRepository.findAll();

        return bookings.stream()
                .map(BookingMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingByUserId(String userId) {

        List<BookingEntity> bookings = bookingRepository.findByUserId(userId);

        return bookings.stream()
                .map(BookingMapper::entityToDto)
                .collect(Collectors.toList());
    }


}
