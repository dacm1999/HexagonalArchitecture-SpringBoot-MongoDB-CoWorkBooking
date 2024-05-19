package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.BookingService;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.domain.enums.BookingStatus;
import com.dacm.hexagonal.domain.model.Booking;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.mapper.BookingMapper;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.BookingPaginationResponse;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.BookingEntity;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class BookingControllerTest {

    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    private BookingController bookingController;

    private static String BASE_URL = "/api/v1/bookings";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        MockitoAnnotations.initMocks(this);
        bookingController = new BookingController(bookingService);
    }

    @Test
    @DisplayName("Test create booking")
    void createBooking() throws Exception {
        UserBookingDto userBookingDto = UserBookingDto.builder()
                .userId("userTest")
                .spaceId("SpaceTest")
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .build();

        ApiResponse apiResponse = new ApiResponse(200, Message.BOOKING_CREATED_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());


        when(bookingService.saveBooking(ArgumentMatchers.any(UserBookingDto.class))).thenReturn(apiResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/create")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(userBookingDto))
                        .accept("application/json"))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Test create multiple bookings")
    void createMultipleBookings() throws Exception {
        UserBookingDto[] bookings = {buildUserBookingDto()};

        ArrayList<UserBookingDto> userBookingDtos = new ArrayList<>();
        userBookingDtos.add(buildUserBookingDto());

        AddedResponse addedResponse = new AddedResponse(true, bookings.length, 1, 0, userBookingDtos, new ArrayList<>());

        when(bookingService.saveMultipleBookings(ArgumentMatchers.any(UserBookingDto[].class))).thenReturn(addedResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/createMultiple")
                        .contentType("application/json")
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(objectMapper.writeValueAsString(bookings)))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @DisplayName("Test confirm booking")
    void confirmBooking() throws Exception {
        String bookingId = "bookingIdTest";

        Booking booking = BookingMapper.toDomain(buildBookingEntityStatus(BookingStatus.PENDING, true));

        when(bookingService.confirmBooking(ArgumentMatchers.any(String.class))).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/confirm/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Test cancel booking")
    void cancelBooking() throws Exception {
        String bookingId = "bookingIdTest";

        Booking booking = BookingMapper.toDomain(buildBookingEntityStatus(BookingStatus.CANCELLED, false));

        when(bookingService.cancelBooking(ArgumentMatchers.any(String.class))).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/cancel/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Test get all bookings")
    void getAllBookings() throws Exception {

        Pageable pageable = Pageable.ofSize(10).withPage(0);
        BookingPaginationResponse bookingPaginationResponse = new BookingPaginationResponse();
        bookingPaginationResponse.setBookings(Collections.singletonList(BookingDto.builder().build()));

        when(bookingService.getAllBookings(pageable)).thenReturn(bookingPaginationResponse);

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/all")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Test get all bookings by start date")
    void getAvailableHoursByDateAndSpace() throws Exception {
        String date = "2024-07-02";
        LocalDateTime localDate = LocalDateTime.parse(date + "T00:00:00");

        List<BookingEntity> bookingEntities = new ArrayList<>();
        bookingEntities.add(buildBookingEntityStatus(BookingStatus.PENDING, true));

        when(bookingService.getBookingsByDate(ArgumentMatchers.any(String.class))).thenReturn(bookingEntities.stream().map(BookingMapper::entityToDto)
                .collect(Collectors.toList()));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/allByStartDate/{startDate}", date)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Test get bookings by status")
    void getBookingsByStatus() throws Exception {

        String status = "PENDING";
        List<BookingEntity> bookingEntities = new ArrayList<>();
        bookingEntities.add(buildBookingEntityStatus(BookingStatus.PENDING, true));

        when(bookingService.getBookingsByStatus(ArgumentMatchers.any(String.class))).thenReturn(bookingEntities.stream().map(BookingMapper::entityToDto)
                .collect(Collectors.toList()));

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/status/{status}", status)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    @DisplayName("Test get booking by booking id")
    void getBookingById() throws Exception {
        String bookingId = "bookingIdTest";

        Booking booking = BookingMapper.toDomain(buildBookingEntityStatus(BookingStatus.PENDING, true));

        when(bookingService.getBookingById(ArgumentMatchers.any(String.class))).thenReturn(booking);
        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();


        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/find/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andReturn();

        assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());


    }

    @Test
    @DisplayName("Test get booking by user id")
    void getBookingsByUserId() throws Exception {
        String userId = "dacm";

        List<BookingEntity> bookingEntities = new ArrayList<>();
        bookingEntities.add(buildBookingEntityStatus(BookingStatus.PENDING, true));

        when(bookingService.getBookingByUserId(ArgumentMatchers.any(String.class))).thenReturn(bookingEntities.stream().map(BookingMapper::entityToDto)
                .collect(Collectors.toList()));

        mockMvc = MockMvcBuilders.standaloneSetup(bookingController).build();

        mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/user/{userId}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Test update booking by booking id")
    void updateBooking() throws Exception {
        String bookingId = "bookingIdTest";
        BookingDto bookingDto = BookingDto.builder().build();
        bookingDto.setActive(false);
        bookingDto.setStatus(BookingStatus.PENDING);

        Booking booking = BookingMapper.toDomain(buildBookingEntityStatus(BookingStatus.CONFIRMED, true));

        when(bookingService.updateBooking(ArgumentMatchers.any(String.class), ArgumentMatchers.any(BookingDto.class))).thenReturn(booking);

        mockMvc.perform(MockMvcRequestBuilders.patch(BASE_URL + "/update/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Test delete booking by booking id")
    void deleteBooking() throws Exception {
        String bookingId = "bookingIdTest";

        ApiResponse apiResponse = new ApiResponse(200, Message.BOOKING_DELETE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());

        when(bookingService.deleteBooking(ArgumentMatchers.any(String.class))).thenReturn(apiResponse);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/delete/{bookingId}", bookingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
    }

    private UserBookingDto buildUserBookingDto() {
        String startTime = "2024-07-02T09:00:00";
        String endTime = "2024-07-02T13:00:00";

        LocalDateTime start = LocalDateTime.parse(startTime);
        LocalDateTime end = LocalDateTime.parse(endTime);

        return UserBookingDto.builder()
                .userId("dacm")
                .spaceId("SpaceTest")
                .startTime(start)
                .endTime(end)
                .build();
    }

    private BookingEntity buildBookingEntityStatus(BookingStatus status, boolean active) {
        SpaceEntity spaceEntity = new SpaceEntity();
        spaceEntity.setSpaceId("spaceIdTest");

        return BookingEntity.builder()
                .id("bookingIdTest")
                .userId("userTest")
                .space(spaceEntity)
                .startTime(LocalDateTime.now())
                .endTime(LocalDateTime.now().plusHours(1))
                .status(status)
                .active(active)
                .build();
    }

}