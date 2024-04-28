package com.dacm.hexagonal.application.mapper;

import com.dacm.hexagonal.infrastructure.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.web.dto.UserDto;

public class UserMapper {

    public static UserDto toDto(UserEntity user){
        return new UserDto(user.getFirstName(), user.getLastName(), user.getEmail(), user.getUsername());
    }
}