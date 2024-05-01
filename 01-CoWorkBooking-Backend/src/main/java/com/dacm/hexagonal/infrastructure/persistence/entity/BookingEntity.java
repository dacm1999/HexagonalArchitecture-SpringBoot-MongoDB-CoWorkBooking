package com.dacm.hexagonal.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Optional;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Bookings")
public class BookingEntity {

    @Id
    private String id;
    @DBRef
    private UserEntity user;
    @DBRef
    private SpaceEntity space;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

}