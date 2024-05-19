package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.UserService;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.domain.model.User;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.UserPaginationResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
     * @param user
     * @return
     */
    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Message.USER_SAVE_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = Message.USER_EMAIL_ALREADY_EXISTS),
            @ApiResponse(responseCode = "400", description = Message.USER_NAME_ALREADY_EXISTS)
    })
    @PostMapping("/create")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.save(user));
    }

    /**
     * Create multiple users
     * @param users
     * @return
     */
    @Operation(summary = "Create multiple users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Message.USER_SAVE_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = Message.USER_EMAIL_ALREADY_EXISTS),
            @ApiResponse(responseCode = "400", description = Message.USER_NAME_ALREADY_EXISTS)
    })
    @PostMapping("/createMultiple")
    public ResponseEntity<AddedResponse> createMultipleUsers(@RequestBody User[] users) {
        return ResponseEntity.ok(userService.saveMultipleUsers(users));
    }

    /**
     * Find user by userId
     * @param userId
     * @return
     */
    @Operation(summary = "Find user by userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Message.USER_FOUND),
            @ApiResponse(responseCode = "400", description = Message.USER_NOT_FOUND)
    })
    @GetMapping("/find/{userId}")
    public ResponseEntity<?> findByUserID(@PathVariable String userId) {
        return ResponseEntity.ok(userService.findByUserId(userId));
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
    @Operation(summary = "Retrieve all users")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Message.USER_FOUND),
            @ApiResponse(responseCode = "400", description = Message.USER_NOT_FOUND)
    })
    @GetMapping("/allUsers")
    public ResponseEntity<?> showAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String firstName,
            @RequestParam(required = false) String lastName,
            @RequestParam(required = false) String email
    ) {
        Pageable pageable = Pageable.ofSize(size).withPage(page);
        Page<UserPaginationResponse> userPage = userService.findAllUsers(username, firstName, lastName, email, pageable);
        UserPaginationResponse response = userPage.getContent().get(0);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete user by userId
     * @param userId
     * @return
     */
    @Operation(summary = "Delete user by userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Message.USER_DELETE_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = Message.USER_NOT_FOUND)
    })
    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<?> deleteByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(userService.deleteByUserId(userId));
    }

    /**
     * Update user by userId
     * @param username
     * @param user
     * @return
     */
    @Operation(summary = "Update user by userId")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = Message.USER_UPDATE_SUCCESSFULLY),
            @ApiResponse(responseCode = "400", description = Message.USER_NOT_FOUND)
    })
    @PutMapping("/update/{username}")
    public ResponseEntity<?> updateByUsername(@PathVariable String username, @RequestBody User user) {
        return ResponseEntity.ok(userService.updateUser(username, user));
    }

}
