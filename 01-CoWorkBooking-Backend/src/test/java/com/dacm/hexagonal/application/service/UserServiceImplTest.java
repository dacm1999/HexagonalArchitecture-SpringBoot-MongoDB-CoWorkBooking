package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.UserService;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.domain.enums.UserRole;
import com.dacm.hexagonal.domain.model.User;
import com.dacm.hexagonal.infrastructure.adapters.input.mapper.UserMapper;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.UserPaginationResponse;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@AutoConfigureMockMvc
class UserServiceImplTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

    private PasswordEncoder passwordEncoder;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        userRepository = mock(UserRepository.class);
        passwordEncoder = mock(PasswordEncoder.class);
        userService = new UserServiceImpl(userRepository, mongoTemplate, passwordEncoder);
    }

    @Test
    @DisplayName("Create user successfully")
    void save() {
        // Arrange
        User user = new User();
        user.setUserId("testUser");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        when(userRepository.existsByUserId(user.getUserId())).thenReturn(false);
        when(passwordEncoder.encode(user.getPassword())).thenReturn("encodedPassword");

        // Act
        ApiResponse response = userService.save(user);

        // Assert
        assertEquals(Message.USER_SAVE_SUCCESSFULLY, response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getTimestamp());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Create user with existing userId")
    void saveWithExistingUserId() {
        // Arrange
        User user = new User();
        user.setUserId("testUser");
        user.setPassword("password123");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setEmail("john.doe@example.com");

        when(userRepository.existsByUserId(user.getUserId())).thenReturn(true);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> userService.save(user));
        verify(userRepository, never()).save(any(UserEntity.class));
    }


    @Test
    @DisplayName("Create multiple users")
    void saveMultipleUsers() {
        // Mock data
        User[] users = {
                new User("user1", "password1", "John", "Doe", "john@example.com"),
                new User("user2", "password2", "Jane", "Smith", "jane@example.com")
        };

        // Mock repository behavior
        when(userRepository.existsByUserId(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // Mock password encoder behavior
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");

        // Execute the method under test
        AddedResponse response = userService.saveMultipleUsers(users);

        // Verify repository method calls
        verify(userRepository, times(2)).save(any());

        // Verify response
        assertEquals(users.length, response.getTotal());
        assertEquals(users.length, response.getAdded().size());
        assertEquals(0, response.getFailed().size());
        assertEquals(users.length, response.getAdded().size());
    }

    @Test
    @DisplayName("Update user successfully")
    void updateUser() {
        User user = new User();
        user.setUserId("testUser");
        user.setFirstName("John");
        user.setLastName("Doe");

        when(userRepository.findByUserId(user.getUserId())).thenReturn(new UserEntity());
        when(userRepository.save(any(UserEntity.class))).thenReturn(new UserEntity());

        ApiResponse response = userService.updateUser(user.getUserId(), user);

        assertEquals(Message.USER_UPDATE_SUCCESSFULLY, response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertNotNull(response.getTimestamp());
        verify(userRepository, times(1)).save(any(UserEntity.class));
    }

    @Test
    @DisplayName("Update user with existing userId")
    void updateUserWithExistingUserIdOrEmail() {
        // Arrange
        String userId = "existingUserId";
        User user = new User();
        user.setUserId(userId);
        user.setEmail("existingEmail@example.com");

        // Mock repository to return a user entity for the given userId
        UserEntity existingUserEntity = new UserEntity();
        existingUserEntity.setUserId(userId);
        when(userRepository.findByUserId(userId)).thenReturn(existingUserEntity);

        // Mock repository to return true for both existsByUserId and existsByEmail methods
        when(userRepository.existsByUserId(userId)).thenReturn(true);

        // Assert and Act for userId already exists
        assertThrows(IllegalArgumentException.class, () -> userService.updateUser(userId, user));
        verify(userRepository, never()).save(any(UserEntity.class));

    }


    @Test
    @DisplayName("Find user by userId")
    void findByUserId() {
        // Arrange
        String userId = "testUser";
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        when(userRepository.findByUserId(userId)).thenReturn(userEntity);

        // Act
        ApiResponse response = userService.findByUserId(userId);

        // Then
        assertEquals("User found", response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
        assertEquals(userEntity.getUserId(), UserMapper.entityToDto(userEntity).getUserId());
    }

    @Test
    @DisplayName("Find non-existing user by userId")
    void findByNonExistingUserId() {
        // Given
        String userId = "nonExistingUser";

        when(userRepository.findByUserId(userId)).thenReturn(null);

        // When
        ApiResponse response = userService.findByUserId(userId);

        // Then
        assertEquals(Message.USER_NOT_FOUND, response.getMessage());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatus());

    }


    @Test
    @DisplayName("Delete user by userId")
    void deleteByUserId() {
        // Arrange
        String userId = "testUser";
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(userId);
        when(userRepository.findByUserId(userId)).thenReturn(userEntity);

        // Act
        ApiResponse response = userService.deleteByUserId(userId);
        assertEquals(Message.USER_DELETE_SUCCESSFULLY, response.getMessage());
        assertEquals(HttpStatus.OK, response.getStatus());
    }

    @Test
    @DisplayName("Delete non-existing user by userId")
    void deleteNonExistingUserByUserId() {
        // Arrange
        String userId = "nonExistingUser";
        when(userRepository.findByUserId(userId)).thenReturn(null);

        // Assert
        assertThrows(UsernameNotFoundException.class, () -> userService.deleteByUserId(userId));
    }

    @Test
    @DisplayName("Find all users")
    void findAllUsers() {
//        // Given
//        String userId = "userId";
//        String lastName = "Doe";
//        String firstName = "John";
//        String email = "john.doe@example.com";
//        Pageable pageable = Pageable.unpaged();
//        List<UserEntity> userEntities = new ArrayList<>();
//        userEntities.add(new UserEntity("1233as", userId, firstName, lastName, email, "password", UserRole.ROLE_USER));
//        long totalElements = 1;
//
//        when(userRepository.findByUserId(userId)).thenReturn(userEntities.get(0)); // Corrige este mock
//
//        // When
//        Page<UserPaginationResponse> response = userService.findAllUsers(userId, lastName, firstName, email, pageable);
//
//        // Then
//        assertEquals(1, response.getTotalElements());
//        assertEquals(1, response.getTotalPages());
//        assertEquals(1, response.getNumberOfElements());
//        assertEquals(1, response.getSize());
//        assertEquals(0, response.getNumber());
    }

    @Test
    @DisplayName("Find all users DTO")
    void findAllUsersDto() {
    }

    @Test
    @DisplayName("Get all User Id")
    void getAllUserId() {
        UserEntity userEntity = UserEntity.builder()
                .userId("testUser")
                .build();

        List<UserEntity> mockUsers = Arrays.asList(
                userEntity);
        when(userRepository.findAll()).thenReturn(mockUsers);

        // Act
        List<String> result = userService.getAllUserId();

        // Assert
        assertEquals(1, result.size());
        assertEquals("testUser", result.get(0));

    }

    @Test
    @DisplayName("Get all emails")
    void getAllEmails() {
        List<UserEntity> mockUsers = Arrays.asList(
                UserEntity.builder()
                        .email("dacm.dev@icloud.com")
                        .build());
        when(userRepository.findAll()).thenReturn(mockUsers);

        // Act
        List<String> result = userService.getAllEmails();

        // Assert
        assertEquals(1, result.size());
        assertEquals("dacm.dev@icloud.com", result.get(0));

    }

}