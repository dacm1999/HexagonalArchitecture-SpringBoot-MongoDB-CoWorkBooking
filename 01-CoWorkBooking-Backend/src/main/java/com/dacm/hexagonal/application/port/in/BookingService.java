package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.infrastructure.persistence.entity.BookingEntity;
import com.dacm.hexagonal.infrastructure.web.dto.BookingRecord;
import com.dacm.hexagonal.infrastructure.web.response.ApiResponse;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

public interface BookingService {

    ApiResponse saveBooking(BookingRecord bookingRecord);

}
