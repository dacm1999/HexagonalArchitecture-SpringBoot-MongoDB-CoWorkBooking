package com.dacm.hexagonal.infrastructure.adapters.input.mapper;

import com.dacm.hexagonal.domain.model.User;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.domain.model.dto.UserDto;

public class UserMapper {

    public static UserDto entityToDto(UserEntity user){
        return new UserDto(user.getFirstName(), user.getLastName(), user.getEmail(), user.getUsername());
    }

    public static UserDto domainToDto(User user){
        return new UserDto(user.getFirstName(), user.getLastName(), user.getEmail(), user.getUsername());
    }
}