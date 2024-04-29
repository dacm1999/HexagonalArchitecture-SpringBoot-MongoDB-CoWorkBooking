package com.dacm.hexagonal.infrastructure.persistence.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "Spaces")
public class SpaceEntity {

    @Id
    private String id;
    private String spaceName; // Name of the space
    private String description; // Description of the space
    private int capacity; // Max capacity of the space
    private List<String> amenities; // Available amenities
    private boolean available; // Is the space available?
    private String location; // Localization of the space

}