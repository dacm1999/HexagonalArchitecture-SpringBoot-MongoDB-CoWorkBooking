package com.dacm.hexagonal.infrastructure.web.controller;

import com.dacm.hexagonal.application.port.in.UserService;
import com.dacm.hexagonal.application.port.out.UserRepository;
import com.dacm.hexagonal.infrastructure.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.web.dto.UserDto;
import com.dacm.hexagonal.infrastructure.web.dto.UserDtoL;
import com.dacm.hexagonal.infrastructure.web.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.web.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.web.response.UserPaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
     * @param userDto
     * @return
     */
    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createProduct(@RequestBody UserDtoL userDto) {
        return ResponseEntity.ok(userService.save(userDto));
    }

    /**
     * Create multiple users
     * @param users
     * @return
     */
    @PostMapping("/createMultiple")
    public ResponseEntity<AddedResponse> createMultipleUsers(@RequestBody UserEntity[] users) {
        return ResponseEntity.ok(userService.saveMultipleUsers(users));
    }

    /**
     * Find user by username
     * @param username
     * @return
     */
    @GetMapping("/find/{username}")
    public ResponseEntity<UserDto> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    /**
     * Show all users
     * @param page
     * @param size
     * @param username
     * @param firstname
     * @param lastname
     * @param email
     * @return
     */
    @GetMapping("/allUsers")
    public ResponseEntity<UserPaginationResponse> showAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstname,
            @RequestParam(required = false) String lastname,
            @RequestParam(required = false) String email
    ) {

        Page<UserDto> userPage = userService.findAllUsers(
                username,
                lastname,
                firstname,
                email,
                Pageable.ofSize(size).withPage(page)
                );
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
     * @param userDto
     * @return
     */
    @PutMapping("/update/{username}")
    public ResponseEntity<ApiResponse> updateByUsername(@PathVariable String username, @RequestBody UserDto userDto) {
        return ResponseEntity.ok(userService.updateUser(username, userDto));
    }

}
