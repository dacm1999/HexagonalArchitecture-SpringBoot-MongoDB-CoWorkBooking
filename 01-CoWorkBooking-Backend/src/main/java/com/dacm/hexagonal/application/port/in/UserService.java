package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.domain.model.User;
import com.dacm.hexagonal.domain.model.dto.UserDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * User Service
 * This class is responsible for handling the user requests
 */
@Service
public interface UserService {

    ApiResponse save(User user);

    AddedResponse saveMultipleUsers(User[] users);

    ApiResponse updateUser(String username, User user);

    ApiResponse deleteByUsername(String username);

    UserDto findByUsername(String username);

    Page<UserDto> findAllUsers(String username, String lastname, String firstname, String email, Pageable pageable);

    List<String> getAllUsernames();

    List<String> getAllEmails();
}
