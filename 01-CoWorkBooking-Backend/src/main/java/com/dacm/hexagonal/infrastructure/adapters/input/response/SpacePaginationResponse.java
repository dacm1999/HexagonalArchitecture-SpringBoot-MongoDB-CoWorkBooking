package com.dacm.hexagonal.infrastructure.adapters.input.response;

import com.dacm.hexagonal.infrastructure.adapters.input.dto.SpaceRecord;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class SpacePaginationResponse {

    private List<SpaceRecord> spaces;
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private int size;
    private int number;

}
