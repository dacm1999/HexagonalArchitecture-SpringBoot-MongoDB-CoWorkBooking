package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.domain.enums.BookingStatus;
import com.dacm.hexagonal.domain.model.Booking;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.mapper.BookingMapper;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.BookingErrorResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.BookingHoursResponse;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
            throw new UsernameNotFoundException(Message.USER_NOT_FOUND + " " + userBookingDto.getUserId());
        }

        SpaceEntity space = spaceRepository.findBySpaceId(userBookingDto.getSpaceId())
                .orElseThrow(() -> new RuntimeException(Message.SPACE_NOT_FOUND + " " + userBookingDto.getSpaceId()));

        LocalDateTime startTime = userBookingDto.getStartTime();
        LocalDateTime endTime = userBookingDto.getEndTime();

        if (startTime.isAfter(endTime)) {
            return new ApiResponse(400, Message.BOOKING_INVALID_START_TIME, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }
        if (startTime.isBefore(LocalDateTime.now())) {
            return new ApiResponse(400, Message.BOOKING_INVALID_TIME, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }
        if (startTime.toLocalTime().isBefore(OPENING_TIME) || endTime.toLocalTime().isAfter(CLOSING_TIME)) {
            return new ApiResponse(400, Message.BOOKING_OUT_OF_OPERATING_HOURS, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        Optional<BookingEntity> existingBooking = bookingRepository.findByUserIdAndSpaceIdAndStartTimeAndEndTime(
                user.getUserId(), space.getId(), startTime, endTime);

        if (existingBooking.isPresent()) {
            return new ApiResponse(409, Message.BOOKING_ALREADY_EXISTS, HttpStatus.CONFLICT, LocalDateTime.now());
        }

        List<BookingEntity> existingBookings = bookingRepository.findBySpaceAndStartTimeBetween(
                space, LocalDateTime.of(startTime.toLocalDate(), OPENING_TIME), LocalDateTime.of(endTime.toLocalDate(), CLOSING_TIME));

        List<LocalTime> availableTimes = findAvailableTimes(existingBookings, startTime.toLocalDate());
        boolean isRequestedTimeAvailable = availableTimes.contains(startTime.toLocalTime());

        if (!isRequestedTimeAvailable) {
            return new ApiResponse(409, Message.BOOKING_TIME_NOT_AVAILABLE, HttpStatus.CONFLICT, LocalDateTime.now(), availableTimes);
        }

        BookingEntity booking = BookingEntity.builder()
                .userId(user.getUserId())
                .space(space)
                .startTime(startTime)
                .endTime(endTime)
                .status(BookingStatus.PENDING)
                .active(true)
                .build();

        bookingRepository.save(booking);
        return new ApiResponse(200, Message.BOOKING_CREATED_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now(), booking.getId());
    }

    @Override
    public AddedResponse saveMultipleBookings(UserBookingDto[] bookingDtos) {

        for (UserBookingDto bookingDto : bookingDtos) {
            saveBooking(bookingDto);
        }
        return null;
    }

    @Override
    public Booking confirmBooking(String bookingId) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            throw new RuntimeException(Message.BOOKING_NOT_FOUND);
        }
        BookingEntity booking = bookingRepository.findById(bookingId).get();
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        return bookingMapper.toDomain(booking);
    }

    @Override
    public Booking cancelBooking(String bookingId) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            throw new RuntimeException(Message.BOOKING_NOT_FOUND);
        }
        BookingEntity booking = bookingRepository.findById(bookingId).get();
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setActive(false);
        bookingRepository.save(booking);
        return bookingMapper.toDomain(booking);
    }

    @Override
    public Booking updateBooking(String bookingId, BookingDto bookingDto) {
        BookingEntity bookingEntity = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException(Message.BOOKING_NOT_FOUND + " " + bookingId));

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
    public List<BookingDto> getBookingsByStatus(String status) {
        if (bookingRepository.findByStatus(status).isEmpty()) {
            throw new RuntimeException(Message.BOOKING_NOT_FOUND);
        }
        List<BookingEntity> bookingEntities = bookingRepository.findByStatus(status);

        return bookingEntities.stream()
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
    public List<BookingDto> getBookingsByStartTime(String startDate) {
        LocalDate date = LocalDate.parse(startDate);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(21, 00, 00);

        List<BookingEntity> bookingEntities = bookingRepository.findByStartTimeBetween(startOfDay, endOfDay);

        return bookingEntities.stream()
                .map(BookingMapper::entityToDto)
                .collect(Collectors.toList());
    }

    private List<LocalTime> findAvailableTimes(List<BookingEntity> bookings, LocalDate date) {
        List<LocalTime> availableTimes = new ArrayList<>();
        LocalTime time = OPENING_TIME;

        while (time.isBefore(CLOSING_TIME)) {
            final LocalTime currentTime = time;
            boolean isAvailable = bookings.stream().noneMatch(
                    booking -> !currentTime.plusHours(1).isBefore(booking.getStartTime().toLocalTime()) &&
                            !currentTime.isAfter(booking.getEndTime().toLocalTime())
            );

            if (isAvailable) {
                availableTimes.add(time);
            }

            time = time.plusHours(1); // Incrementar en bloques de 1 hora o el intervalo deseado
        }

        return availableTimes;
    }

}
