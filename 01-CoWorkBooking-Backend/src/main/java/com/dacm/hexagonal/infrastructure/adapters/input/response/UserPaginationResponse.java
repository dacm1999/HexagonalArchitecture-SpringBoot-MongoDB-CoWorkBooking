package com.dacm.hexagonal.infrastructure.adapters.input.response;

import com.dacm.hexagonal.infrastructure.adapters.input.dto.UserDto;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@RequiredArgsConstructor
public class UserPaginationResponse {

    private List<UserDto> users;
    private long totalElements;
    private int totalPages;
    private int numberOfElements;
    private int size;
    private int number;

}
