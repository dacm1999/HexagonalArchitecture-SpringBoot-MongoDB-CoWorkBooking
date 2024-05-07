package com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity;

import com.dacm.hexagonal.domain.enums.Status;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Bookings")
public class BookingEntity {

    @Id
    private String id;
    @Field("userId")
    private String userId;
    @DBRef
    private SpaceEntity space;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Status status;
    private boolean active;

}
