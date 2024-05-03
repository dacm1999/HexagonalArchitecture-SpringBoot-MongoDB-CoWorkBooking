package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.domain.model.dto.UserBookingDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.BookingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    ApiResponse saveBooking(UserBookingDto bookingRecord);
    Page<BookingDto> getBookingsByUser(String username, Pageable pageable);
    Page<BookingEntity> getSpacesBySpaceId(String spaceId, Pageable pageable);

}
