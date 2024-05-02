package com.dacm.hexagonal.infrastructure.adapters.input.mapper;

import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.adapters.input.dto.UserDto;

public class UserMapper {

    public static UserDto toDto(UserEntity user){
        return new UserDto(user.getFirstName(), user.getLastName(), user.getEmail(), user.getUsername());
    }
}