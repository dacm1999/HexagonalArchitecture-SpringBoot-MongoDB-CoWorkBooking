package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.UserService;
import com.dacm.hexagonal.domain.model.User;
import com.dacm.hexagonal.domain.model.dto.UserDto;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.UserPaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

/**
 * User Controller
 * This class is responsible for handling the user requests
 * @version 1.0
 * @see UserService
 * @see UserRepository
 * @see UserEntity
 * @author danie
 */
@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final UserService userService;


    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Create a new user
     * @param user
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
    }

    /**
     * Create multiple users
     * @param users
     * @return
     */
    @PostMapping("/createMultiple")
    public ResponseEntity<AddedResponse> createMultipleUsers(@RequestBody User[] users) {
        return ResponseEntity.ok(userService.saveMultipleUsers(users));
    }

    /**
     * Find user by username
     * @param username
     * @return
     */
    @GetMapping("/find/{username}")
    public ResponseEntity<?> findByUsername(@PathVariable String username) {
        UserDto userDto = userService.findByUsername(username);

        if(userDto == null) {
            return ResponseEntity.badRequest().body(new ApiResponse(
                    HttpStatus.NOT_FOUND.value(),
                    Message.USER_NOT_FOUND,
                    HttpStatus.NOT_FOUND,
                    LocalDateTime.now()));
        }
        return ResponseEntity.ok(userDto);
    }

    /**
     * Show all users
     * @param page
     * @param size
     * @param username
     * @param firstName
     * @param lastName
     * @param email
     * @return
     */
    @GetMapping("/allUsers")
    public ResponseEntity<UserPaginationResponse> showAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email
    ) {

        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserDto> userPage = userService.findAllUsers(username, firstName, lastName, email, pageable);

        UserPaginationResponse response = new UserPaginationResponse();
        response.setUsers(userPage.getContent());
        response.setTotalPages(userPage.getTotalPages());
        response.setTotalElements(userPage.getTotalElements());
        response.setNumber(userPage.getNumber());
        response.setNumberOfElements(userPage.getNumberOfElements());
        response.setSize(userPage.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * Delete user by username
     * @param username
     * @return
     */
    @DeleteMapping("/delete/{username}")
    public ResponseEntity<ApiResponse> deleteByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.deleteByUsername(username));
    }

    /**
     * Update user by username
     * @param username
     * @param user
     * @return
     */
    @PutMapping("/update/{username}")
    public ResponseEntity<ApiResponse> updateByUsername(@PathVariable String username, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(username, user));
    }

}
