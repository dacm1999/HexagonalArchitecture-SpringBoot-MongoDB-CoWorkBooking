package com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository;

import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.BookingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends MongoRepository<BookingEntity, String> {

}
