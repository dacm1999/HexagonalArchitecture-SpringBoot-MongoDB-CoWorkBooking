package com.dacm.hexagonal.domain.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;

import javax.annotation.processing.Generated;

@Data
@Builder
public class Space {

    @Id
    @Generated(value = "space")
    private String id;
    private String name;
    private String description;
    private String capacity;
    private String isAvailable; // The space is available or not


}
