package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.infrastructure.web.dto.UserDto;
import com.dacm.hexagonal.infrastructure.web.dto.UserDtoL;
import com.dacm.hexagonal.infrastructure.web.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * User Service
 * This class is responsible for handling the user requests
 */
@Service
public interface UserService {

    ApiResponse save (UserDtoL user);

    ApiResponse updateUser(String username, UserDto userDto);

    ApiResponse deleteByUsername(String username);

    UserDto findByUsername(String username);

    Page<UserDto> findAllUsers(String username, String lastname, String firstname, String email, Pageable pageable);
}
