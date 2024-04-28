package com.dacm.hexagonal.infrastructure.web.controller;

import com.dacm.hexagonal.application.port.in.UserService;
import com.dacm.hexagonal.infrastructure.web.dto.UserDto;
import com.dacm.hexagonal.infrastructure.web.dto.UserDtoL;
import com.dacm.hexagonal.common.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;


@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private final UserService userService;


    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createProduct(@RequestBody UserDtoL userDto) {
        return ResponseEntity.ok(userService.save(userDto));
    }

    @GetMapping("/find/{username}")
    public ResponseEntity<UserDto> findByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.findByUsername(username));
    }

    @DeleteMapping("/delete/{username}")
    public ResponseEntity<ApiResponse> deleteByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.deleteByUsername(username));
    }


}
