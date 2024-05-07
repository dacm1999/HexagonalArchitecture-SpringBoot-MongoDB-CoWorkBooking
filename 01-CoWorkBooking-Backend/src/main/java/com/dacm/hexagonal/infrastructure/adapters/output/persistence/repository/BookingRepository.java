package com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository;

import com.dacm.hexagonal.domain.model.Booking;
import com.dacm.hexagonal.domain.model.dto.BookingDto;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.BookingEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends MongoRepository<BookingEntity, String> {

    //    Page<BookingEntity> findByUserUsername(String username, Pageable pageable);
//    Page<BookingDto> findByUserId(String id, Pageable pageable);
        List<BookingEntity> findByUserId(String id);

//    Page<BookingEntity> findByUserUsername(String userId, Pageable pageable);

}
