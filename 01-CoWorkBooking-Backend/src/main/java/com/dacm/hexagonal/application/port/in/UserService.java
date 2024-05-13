package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.domain.model.User;
import com.dacm.hexagonal.domain.model.dto.UserDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.UserPaginationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * User Service
 * This class is responsible for handling the user requests
 */
public interface UserService {

    ApiResponse save(User user);

    AddedResponse saveMultipleUsers(User[] users);

    ApiResponse updateUser(String userId, User user);

    ApiResponse deleteByUserId(String userId);

    ApiResponse findByUserId(String userId);

    Page<UserPaginationResponse> findAllUsers(String userId, String lastname, String firstname, String email, Pageable pageable);

    Page<UserDto> findAllUsersDto(String userId, String lastName, String firstName, String email, Pageable pageable);

    List<String> getAllUsernames();

    List<String> getAllEmails();
}
