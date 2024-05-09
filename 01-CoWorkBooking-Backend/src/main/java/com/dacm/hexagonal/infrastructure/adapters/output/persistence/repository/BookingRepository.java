package com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository;

import com.dacm.hexagonal.domain.model.Booking;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.BookingEntity;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends MongoRepository<BookingEntity, String> {

    List<BookingEntity> findByUserId(String id);

    List<BookingEntity> findByStatus(String status);

    @Query("{'startTime': {$gte: ?0, $lt: ?1}}")
    List<BookingEntity> findByStartTimeBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    @Query("{ 'spaceId': ?0, 'startTime': { $lt: ?2 }, 'endTime': { $gt: ?1 }, 'active': true }")
    List<BookingEntity> findBySpaceAndDate(String spaceId, LocalDateTime startOfDay, LocalDateTime endOfDay);

}
