package com.dacm.hexagonal.infrastructure.adapters.input.mapper;

import com.dacm.hexagonal.domain.model.Booking;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.BookingEntity;
import org.springframework.stereotype.Component;

/**
 * Provides mapping functionality to convert between BookingEntity (persistence layer),
 * Booking (domain layer), and BookingDto (data transfer object).
 */
@Component
public class BookingMapper {

    /**
     * Converts a BookingEntity to a Booking domain model. This method assumes the existence of UserMapper
     * and SpaceMapper to handle the conversion of related UserEntity and SpaceEntity.
     *
     * @param entity the BookingEntity to convert.
     * @return Booking the domain model of Booking if entity is not null; otherwise, returns null.
     */
    public static Booking toDomain(BookingEntity entity) {
        if (entity == null) {
            return null;
        }

        return Booking.builder()
                .id(entity.getId())
                .id(entity.getUserId())
//                .user(UserMapper.toDomain(entity.getUser())) // Assuming you have a UserMapper
                .space(SpaceMapper.toDomain(entity.getSpace())) // Assuming you have a SpaceMapper
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .active(entity.isActive())
                .build();
    }

    /**
     * Converts a Booking domain model to a BookingDto. This method is used to create data transfer objects
     * that can be used in upper layers (like web or API layers) to reduce the amount of information sent over
     * and tailor it for client use.
     *
     * @param booking the Booking domain model to convert.
     * @return BookingDto the data transfer object if booking is not null; otherwise, returns null.
     */
    public static BookingDto toDto(Booking booking) {
        if (booking == null) {
            return null;
        }

        return BookingDto.builder()
                .spaceId(booking.getSpace().getId()) // Assuming Space object has getId()
                .startTime(booking.getStartTime())
                .endTime(booking.getEndTime())
                .active(booking.isActive())
                .build();
    }

    public static BookingDto entityToDto(BookingEntity entity) {
        if (entity == null) {
            return null;
        }

        // Asumiendo que tienes campos id, startTime, etc., en tu BookingEntity y BookingDto
        return BookingDto.builder()
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .active(entity.isActive())
                .build();
    }

    /**
     * Converts a BookingDto to a BookingEntity.
     *
     * @param dto the BookingDto to convert.
     * @return BookingEntity the persistence entity if dto is not null; otherwise, returns null.
     */
    public static BookingEntity dtoToEntity(BookingDto dto) {
        if (dto == null) {
            return null;
        }

        BookingEntity entity = new BookingEntity();
        entity.setStartTime(dto.getStartTime());
        entity.setEndTime(dto.getEndTime());
        entity.setActive(dto.isActive());
        // Assume that you have a way to set the User and Space based on ids or session context
        // entity.setUser(UserMapper.dtoToEntity(dto.getUserDto())); // Example if you had user data
        // entity.setSpace(SpaceMapper.dtoToEntity(dto.getSpaceDto())); // Example if you had space data
        return entity;
    }
}
