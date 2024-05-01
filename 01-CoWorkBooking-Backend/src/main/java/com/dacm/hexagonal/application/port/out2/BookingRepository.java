package com.dacm.hexagonal.application.port.out2;

import com.dacm.hexagonal.infrastructure.persistence.entity.BookingEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookingRepository extends MongoRepository<BookingEntity, String> {

}
