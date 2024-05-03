package com.dacm.hexagonal.domain.model;

import lombok.*;
import org.springframework.data.annotation.Id;

import javax.annotation.processing.Generated;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Space {

    private String id;
    private String spaceId; // Unique identifier of the space
    private String spaceName; // Name of the space
    private String description; // Description of the space
    private int capacity; // Max capacity of the space
    private List<String> amenities; // Available amenities
    private boolean available; // Is the space available?
    private String location; // Localization of the space


}
