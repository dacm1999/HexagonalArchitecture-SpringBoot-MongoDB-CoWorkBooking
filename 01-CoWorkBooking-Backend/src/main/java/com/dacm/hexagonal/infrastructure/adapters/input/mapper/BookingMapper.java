package com.dacm.hexagonal.infrastructure.adapters.input.mapper;

import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.BookingEntity;
import org.springframework.stereotype.Component;

@Component
public class BookingMapper {

    public BookingDto bookingEntityToDto(BookingEntity bookingEntity) {
        if (bookingEntity == null) {
            return null;
        }

        BookingDto bookingDto = new BookingDto();
        bookingDto.setSpaceId(bookingEntity.getSpaceId());
        bookingDto.setStartTime(bookingEntity.getStartTime());
        bookingDto.setEndTime(bookingEntity.getEndTime());
        bookingDto.setActive(bookingEntity.isActive());
        return bookingDto;
    }

    public BookingEntity bookingDtoToEntity(BookingDto bookingDto) {
        if (bookingDto == null) {
            return null;
        }
        BookingEntity bookingEntity = BookingEntity.builder()
                .spaceId(bookingDto.getSpaceId())
                .startTime(bookingDto.getStartTime())
                .endTime(bookingDto.getEndTime())
                .active(bookingDto.isActive())
                .build();
        return bookingEntity;
    }
}
