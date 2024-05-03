package com.dacm.hexagonal.infrastructure.adapters.input.response;

import com.dacm.hexagonal.domain.model.dto.SpaceDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class SpacePaginationResponse {

    private List<SpaceDto> spaces;
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private int size;
    private int number;

}
