package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.application.port.in.UserService;
import com.dacm.hexagonal.domain.enums.BookingStatus;
import com.dacm.hexagonal.domain.model.Booking;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.mapper.BookingMapper;
import com.dacm.hexagonal.infrastructure.adapters.input.mapper.SpaceMapper;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.BookingHoursResponse;
import com.dacm.hexagonal.infrastructure.adapters.output.email.EmailService;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.BookingRepository;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.SpaceRepository;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.BookingEntity;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {

    private final EmailService emailService;
    private final UserRepository userRepository;
    private final SpaceRepository spaceRepository;
    private final BookingRepository bookingRepository;
    private final MongoTemplate mongoTemplate;
    private static final LocalTime OPENING_TIME = LocalTime.of(9, 0); // 9:00 AM
    private static final LocalTime CLOSING_TIME = LocalTime.of(21, 0); // 9:00 PM


    @Autowired
    public BookingServiceImpl(UserRepository userRepository, SpaceRepository spaceRepository, BookingRepository bookingRepository, MongoTemplate mongoTemplate, SpaceMapper spaceMapper, EmailService emailService) {
        this.userRepository = userRepository;
        this.spaceRepository = spaceRepository;
        this.bookingRepository = bookingRepository;
        this.mongoTemplate = mongoTemplate;
        this.emailService = emailService;
    }


    /**
     * Saves a new booking in the database.
     * Validates booking times, checks for overlapping bookings, and confirms the space availability.
     *
     * @param userBookingDto DTO containing all necessary data for creating a new booking.
     * @return ApiResponse indicating success or failure of the booking creation.
     */
    @Override
    public ApiResponse saveBooking(UserBookingDto userBookingDto) throws MessagingException {
        LocalDateTime startTime = userBookingDto.getStartTime();
        LocalDateTime endTime = userBookingDto.getEndTime();

        DateTimeFormatter formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM, FormatStyle.SHORT);
        String formattedStartTime = startTime.format(formatter);
        String formattedEndTime = endTime.format(formatter);

        UserEntity user = userRepository.findByUserId(userBookingDto.getUserId());
        if (user == null) {
            throw new UsernameNotFoundException(Message.USER_NOT_FOUND);
        }

        SpaceEntity space = spaceRepository.findBySpaceId(userBookingDto.getSpaceId())
                .orElseThrow(() -> new RuntimeException(Message.SPACE_NOT_FOUND + " " + userBookingDto.getSpaceId()));

        // Check if start time is after end time
        if (startTime.isAfter(endTime)) {
            return new ApiResponse(400, Message.BOOKING_INVALID_START_TIME, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        // Check if start time is before current time
        if (startTime.isBefore(LocalDateTime.now())) {
            return new ApiResponse(400, Message.BOOKING_INVALID_TIME, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        // Check if booking is within operating hours
        if (startTime.toLocalTime().isBefore(OPENING_TIME) || endTime.toLocalTime().isAfter(CLOSING_TIME)) {
            return new ApiResponse(400, Message.BOOKING_OUT_OF_OPERATING_HOURS, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }

        List<BookingEntity> overlappingBookings = bookingRepository.findBySpaceAndDate(space.getId(), startTime, endTime);
        if (!overlappingBookings.isEmpty()) {
            List<LocalTime> availableHours = findAvailableBookingHours(space, startTime.toLocalDate());
            System.out.println("Available hours: " + availableHours); // Depuración
            return new ApiResponse(409, Message.BOOKING_TIME_NOT_AVAILABLE, HttpStatus.CONFLICT, LocalDateTime.now(), availableHours);
        }

        Model model = new ExtendedModelMap();
        String fullName = user.getFirstName() + " " + user.getLastName();
        model.addAttribute("fullName", fullName);
        model.addAttribute("spaceId", space.getSpaceId());
        model.addAttribute("userBookingDto", userBookingDto);
        model.addAttribute("startTime", formattedStartTime);
        model.addAttribute("endTime", formattedEndTime);

        BookingEntity booking = BookingEntity.builder()
                .userId(user.getUserId())
                .space(space)
                .startTime(startTime)
                .endTime(endTime)
                .status(BookingStatus.PENDING)
                .active(true)
                .build();

        bookingRepository.save(booking);
        emailService.sendHtmlMessage(user.getEmail(), "Booking Confirmation", model, "booking-created.html");
        return new ApiResponse(200, Message.BOOKING_CREATED_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now(), BookingMapper.entityToDto(booking));
    }


    /**
     * Saves multiple bookings at once.
     * Iterates over an array of UserBookingDto to save each booking.
     *
     * @param bookingDtos Array of UserBookingDto containing booking details.
     * @return AddedResponse with details about the added bookings.
     */
    @Override
    public AddedResponse saveMultipleBookings(UserBookingDto[] bookingDtos) throws MessagingException {

        for (UserBookingDto bookingDto : bookingDtos) {
            saveBooking(bookingDto);
        }
        return null;
    }

    /**
     * Confirms a booking based on its ID.
     * Changes the booking status to CONFIRMED.
     *
     * @param bookingId ID of the booking to confirm.
     * @return The updated Booking domain model.
     */
    @Override
    public Booking confirmBooking(String bookingId) throws MessagingException {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            throw new RuntimeException(Message.BOOKING_NOT_FOUND);
        }
        BookingEntity booking = bookingRepository.findById(bookingId).get();
        UserEntity user = userRepository.findByUserId(booking.getUserId());
        booking.setStatus(BookingStatus.CONFIRMED);
        Model model = new ExtendedModelMap();
        model.addAttribute("bookingId", booking.getId());
        emailService.sendHtmlMessage(user.getEmail(), "Booking Confirmation", model, "booking-confirmation.html");
        bookingRepository.save(booking);

        return BookingMapper.toDomain(booking);
    }

    /**
     * Cancels a booking based on its ID.
     * Changes the booking status to CANCELLED and sets it as inactive.
     *
     * @param bookingId ID of the booking to cancel.
     * @return The updated Booking domain model.
     */
    @Override
    public Booking cancelBooking(String bookingId) throws MessagingException {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            throw new RuntimeException(Message.BOOKING_NOT_FOUND);
        }
        BookingEntity booking = bookingRepository.findById(bookingId).get();
        UserEntity user = userRepository.findByUserId(booking.getUserId());
        booking.setStatus(BookingStatus.CANCELLED);
        booking.setActive(false);
        Model model = new ExtendedModelMap();
        emailService.sendHtmlMessage(user.getEmail(), "Booking Cancelled", model, "booking-cancelled.html");
        bookingRepository.save(booking);
        return BookingMapper.toDomain(booking);
    }

    /**
     * Updates a booking details such as start time, end time, status, and active state.
     *
     * @param bookingId  ID of the booking to update.
     * @param bookingDto DTO with the updated booking data.
     * @return The updated Booking domain model.
     */
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
        return BookingMapper.toDomain(bookingEntity);
    }

    /**
     * Retrieves a booking by its ID.
     *
     * @param bookingId ID of the booking to retrieve.
     * @return Booking domain model of the retrieved booking.
     */
    @Override
    public Booking getBookingById(String bookingId) {
        BookingEntity booking = bookingRepository.findById(bookingId).
                orElseThrow(() -> new RuntimeException("Booking not found"));

        return BookingMapper.toDomain(booking);
    }

    /**
     * Retrieves all bookings with pagination.
     *
     * @param pageable Pageable object to configure pagination.
     * @return Page of BookingDto containing paginated booking data.
     */
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

    /**
     * Retrieves all bookings made by a specific user.
     *
     * @param userId ID of the user whose bookings are to be retrieved.
     * @return List of BookingDto containing bookings of the specified user.
     */
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

    /**
     * Retrieves all bookings with a specific status.
     *
     * @param status Status of the bookings to retrieve.
     * @return List of BookingDto with bookings having the specified status.
     */
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

    /**
     * Deletes a booking based on its ID.
     *
     * @param bookingId ID of the booking to delete.
     * @return ApiResponse indicating the result of the deletion process.
     */
    @Override
    public ApiResponse deleteBooking(String bookingId) {
        if (bookingRepository.findById(bookingId).isEmpty()) {
            return new ApiResponse(400, Message.BOOKING_NOT_FOUND, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }
        BookingEntity booking = bookingRepository.findById(bookingId).get();
        bookingRepository.delete(booking);
        return new ApiResponse(200, Message.BOOKING_DELETE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now(), BookingMapper.entityToDto(booking));
    }

    /**
     * Retrieves all bookings on a specific date.
     *
     * @param startDate String representation of the start date.
     * @return List of BookingDto for bookings on the specified date.
     */
    @Override
    public List<BookingDto> getBookingsByDate(String startDate) {
        LocalDate date = LocalDate.parse(startDate);
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(21, 00, 00);

        List<BookingEntity> bookingEntities = bookingRepository.findByStartTimeBetween(startOfDay, endOfDay);

        return bookingEntities.stream()
                .map(BookingMapper::entityToDto)
                .collect(Collectors.toList());
    }


    /**
     * Retrieves available booking hours for a given space and date.
     *
     * @param spaceId ID of the space.
     * @param date    String representation of the date.
     * @return ApiResponse with available booking hours.
     */
    @Override
    public ApiResponse getAvailableHoursByDateAndSpace(String spaceId, String date) {
        SpaceEntity space = spaceRepository.findBySpaceId(spaceId)
                .orElseThrow(() -> new RuntimeException(Message.SPACE_NOT_FOUND + " " + spaceId));

        LocalDate date1 = LocalDate.parse(date);
        List<LocalTime> availableHours = findAvailableBookingHours(space, date1);

        if (availableHours.isEmpty()) {
            return new ApiResponse(HttpStatus.BAD_REQUEST.value(), Message.NO_AVAILABLE_HOURS, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }
        BookingHoursResponse response = new BookingHoursResponse();
        response.setAvailableHours(availableHours);
        return new ApiResponse(200, Message.AVAILABLE_HOURS, HttpStatus.OK, LocalDateTime.now(), response);
    }

    /**
     * Finds available booking hours within a day for a given space.
     * Checks against existing bookings to determine open slots.
     *
     * @param space SpaceEntity for which to find available hours.
     * @param date  LocalDate representing the day to check for availability.
     * @return List of LocalTime representing available hours.
     */
    public List<LocalTime> findAvailableBookingHours(SpaceEntity space, LocalDate date) {
        LocalDateTime startOfDay = date.atTime(OPENING_TIME);
        LocalDateTime endOfDay = date.atTime(CLOSING_TIME);
//        LocalDateTime startOfDay = date.atTime(OPENING_TIME).withSecond(0).withNano(0);
//        LocalDateTime endOfDay = date.atTime(CLOSING_TIME).withSecond(0).withNano(0);

        List<BookingEntity> bookings = bookingRepository.findBySpaceAndDate(space.getId(), startOfDay, endOfDay);
        System.out.println("Total de reservas encontradas para el día: " + bookings.size());

        List<LocalTime> availableHours = new ArrayList<>();
        for (LocalTime time = OPENING_TIME; !time.isAfter(CLOSING_TIME.minusHours(1)); time = time.plusHours(1)) {
            LocalDateTime slotStart = date.atTime(time);
            LocalDateTime slotEnd = slotStart.plusHours(1);

            // verify if the slot is available
            boolean isAvailable = bookings.stream()
                    .noneMatch(booking -> booking.getStartTime().isBefore(slotEnd) && booking.getEndTime().isAfter(slotStart));

            if (isAvailable) {
                availableHours.add(time);
            }
        }
        System.out.println("Horas disponibles calculadas: " + availableHours);
        return availableHours;
    }

}
