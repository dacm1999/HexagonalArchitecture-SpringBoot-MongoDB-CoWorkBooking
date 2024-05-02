package com.dacm.hexagonal.infrastructure.adapters.input.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SpaceErrorResponse {

    String spaceName;
    String errorDescription;

    public SpaceErrorResponse(String spaceName, String errorDescription) {
        this.spaceName = spaceName;
        this.errorDescription = errorDescription;
    }
}
