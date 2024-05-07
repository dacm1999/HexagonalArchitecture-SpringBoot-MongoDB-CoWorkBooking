package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.domain.enums.Status;
import com.dacm.hexagonal.domain.model.Booking;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.mapper.BookingMapper;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.BookingErrorResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.BookingPaginationResponse;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.BookingRepository;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.SpaceRepository;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.BookingEntity;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
    private static final LocalTime OPENING_TIME = LocalTime.of(9, 0); // 9:00 AM
    private static final LocalTime CLOSING_TIME = LocalTime.of(21, 0); // 9:00 PM


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
        UserEntity user = userRepository.findByUserId(userBookingDto.getUserId());
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        // Check if space exists
        SpaceEntity space = spaceRepository.findBySpaceId(userBookingDto.getSpaceId())
                .orElseThrow(() -> new RuntimeException("Space not found"));

        // Check if space is available
//        if (!space.isAvailable()) {
//            return new ApiResponse(400, Message.SPACE_NOT_AVAILABLE, HttpStatus.BAD_REQUEST, LocalDateTime.now());
//        }

        // Check if booking start time is after end time
        if (userBookingDto.getStartTime().isAfter(userBookingDto.getEndTime())) {
            return new ApiResponse(400, Message.BOOKING_INVALID_START_TIME, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        // Check if booking start time is before current time
        if (userBookingDto.getStartTime().isBefore(LocalDateTime.now())) {
            return new ApiResponse(400, Message.BOOKING_INVALID_TIME, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        if (userBookingDto.getStartTime().toLocalTime().isBefore(OPENING_TIME) ||
                userBookingDto.getEndTime().toLocalTime().isAfter(CLOSING_TIME)) {
            return new ApiResponse(400, Message.BOOKING_OUT_OF_OPERATING_HOURS, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        // Set space as unavailable
//        spaceService.changeSpaceAvailability(space, false);

        BookingEntity booking = BookingEntity.builder().
                userId(userBookingDto.getUserId()).
                space(space).
                startTime(userBookingDto.getStartTime()).
                endTime(userBookingDto.getEndTime()).
                status(Status.PENDING).
                active(true).
                build();


        bookingRepository.save(booking);

        return new ApiResponse(200, Message.BOOKING_CREATED_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now(), booking.getId());
    }

    @Override
    public AddedResponse saveMultipleBookings(UserBookingDto[] bookingDtos) {
        List<UserBookingDto> addedBookings = new ArrayList<>();
        List<BookingErrorResponse> failedBookings = new ArrayList<>();
        String description = "";


        return null;
    }

    @Override
    public Booking confirmBooking(String bookingId) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            throw new RuntimeException(Message.BOOKING_NOT_FOUND);
        }
        BookingEntity booking = bookingRepository.findById(bookingId).get();
        booking.setStatus(Status.CONFIRMED);
        bookingRepository.save(booking);
        return bookingMapper.toDomain(booking);
    }

    @Override
    public Booking cancelBooking(String bookingId) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            throw new RuntimeException(Message.BOOKING_NOT_FOUND);
        }
        BookingEntity booking = bookingRepository.findById(bookingId).get();
        booking.setStatus(Status.CANCELLED);
        booking.setActive(false);
        bookingRepository.save(booking);
        return bookingMapper.toDomain(booking);
    }

    @Override
    public Booking updateBooking(String bookingId, BookingDto bookingDto) {
        BookingEntity bookingEntity = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException(Message.BOOKING_NOT_FOUND+ " " + bookingId));

        if (bookingDto.getStartTime() != null) {
            bookingEntity.setStartTime(bookingDto.getStartTime());
        }
        if (bookingDto.getEndTime() != null) {
            bookingEntity.setEndTime(bookingDto.getEndTime());
        }
        if (bookingDto.getStatus() != null) {
            bookingEntity.setStatus(bookingDto.getStatus());
        }
        if (bookingDto.isActive() != bookingEntity.isActive()) {
            bookingEntity.setActive(bookingDto.isActive());
        }

        bookingRepository.save(bookingEntity);
        return bookingMapper.toDomain(bookingEntity);
    }

    @Override
    public Page<BookingDto> getAllBookings(Pageable pageable) {

        Query query = new Query().with(pageable);
        List<BookingEntity> bookings = mongoTemplate.find(query, BookingEntity.class);
        long total = mongoTemplate.count(new Query(), BookingEntity.class);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::entityToDto)
                .collect(Collectors.toList());


        return new PageImpl<>(bookingDtos, pageable, total);
    }

    @Override
    public Booking getBookingById(String bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId).
                orElseThrow(() -> new RuntimeException("Booking not found"));

        return bookingMapper.toDomain(booking);
    }

    @Override
    public List<BookingDto> getBookingByUserId(String userId) {
        if (bookingRepository.findByUserId(userId).isEmpty()) {
            throw new UsernameNotFoundException(Message.USER_WITHOUT_BOOKING + " " + userId);
        }
        List<BookingEntity> bookingEntity = bookingRepository.findByUserId(userId);

        return bookingEntity.stream()
                .map(BookingMapper::entityToDto)
                .collect(Collectors.toList());
    }

    @Override
    public ApiResponse deleteBooking(String bookingId) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            return new ApiResponse(400, Message.BOOKING_NOT_FOUND, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }
        BookingEntity booking = bookingRepository.findById(bookingId).get();
        bookingRepository.delete(booking);
        return new ApiResponse(200, Message.BOOKING_DELETE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now(), BookingMapper.entityToDto(booking));
    }

    @Override
    public Page<BookingDto> getAllBookingsByStartDate(String startDate, Pageable pageable) {
        Query query = new Query().with(pageable);
        List<BookingEntity> bookings = mongoTemplate.find(query, BookingEntity.class);
        long total = mongoTemplate.count(new Query(), BookingEntity.class);

        List<BookingDto> bookingDtos = bookings.stream()
                .map(BookingMapper::entityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(bookingDtos, pageable, total);
    }


}
