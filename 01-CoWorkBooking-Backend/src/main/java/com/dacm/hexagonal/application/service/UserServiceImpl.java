package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.mapper.UserMapper;
import com.dacm.hexagonal.application.port.in.UserService;
import com.dacm.hexagonal.application.port.out.UserRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.infrastructure.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.web.dto.UserDto;
import com.dacm.hexagonal.infrastructure.web.dto.UserDtoL;
import com.dacm.hexagonal.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public ApiResponse save(UserDtoL user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(user.firstName());
        userEntity.setLastName(user.lastName());
        userEntity.setEmail(user.email());
        userEntity.setUsername(user.username());
        userEntity.setPassword(user.password());
        userRepository.save(userEntity);
        return new ApiResponse( 201,Message.USER_SAVE_SUCCESSFULLY ,HttpStatus.CREATED, LocalDateTime.now());
    }

    @Override
    public UserDto findByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException(Message.USER_NOT_FOUND + " " + username));
        return UserMapper.toDto(userEntity);
    }

    @Override
    public ApiResponse deleteByUsername(String username) {
        UserDto userDto = findByUsername(username);
        if (userDto == null) {
            throw new UsernameNotFoundException(Message.USER_NOT_FOUND + " " + username);
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUsername(username);
        userEntity.setFirstName(userDto.getFirstName());
        userEntity.setLastName(userDto.getLastName());
        userEntity.setEmail(userDto.getEmail());
        userRepository.deleteByUsername(username);
        return new ApiResponse(204, Message.USER_DELETE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());
    }


}
