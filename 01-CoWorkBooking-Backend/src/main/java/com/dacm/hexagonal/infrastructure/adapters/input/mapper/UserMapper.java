package com.dacm.hexagonal.infrastructure.adapters.input.mapper;

import com.dacm.hexagonal.domain.model.User;
import com.dacm.hexagonal.domain.model.dto.UserDto;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    /**
     * Converts a UserEntity to a UserDto.
     *
     * @param user The UserEntity to convert.
     * @return UserDto with selected fields from the UserEntity.
     */
    public static UserDto entityToDto(UserEntity user) {
        return new UserDto(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUserId());
    }

    /**
     * Converts a User domain model to a UserDto.
     *
     * @param user The User domain model to convert.
     * @return UserDto with selected fields from the User domain model.
     */
    public static UserDto domainToDto(User user) {
        return new UserDto(
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getUserId()
        );
    }

    /**
     * Converts a UserEntity to a User domain model.
     *
     * @param entity The UserEntity to convert.
     * @return User domain model with data from the UserEntity.
     * Note: This method includes the user's password. Care should be taken when handling and storing passwords.
     */
    public static User toDomain(UserEntity entity) {
        if (entity == null) {
            return null;
        }

        return User.builder()
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .email(entity.getEmail())
                .userId(entity.getUserId())
                .build();
    }

}