package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.mapper.UserMapper;
import com.dacm.hexagonal.application.port.in.UserService;
import com.dacm.hexagonal.application.port.out.UserRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.infrastructure.persistence.entity.UserEntity;
import com.dacm.hexagonal.infrastructure.web.dto.UserDto;
import com.dacm.hexagonal.infrastructure.web.dto.UserDtoL;
import com.dacm.hexagonal.infrastructure.web.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Implementation of the user service that manages business logic related to users.
 * This class implements the UserService interface, providing concrete functionalities for
 * saving, updating, finding, and deleting users, as well as listing all users with
 * the ability to apply filters and pagination.
 *
 * @author UserRepository is the repository used for database operations.
 * @author MongoTemplate is used for more complex queries and operations that require more
 * flexibility than the standard methods provided by the repository.
 * @version 1.0 An initial version that provides basic methods for user management.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    private MongoTemplate mongoTemplate;

    /**
     * Constructor for injecting necessary dependencies for repository operations and MongoDB.
     *
     * @param userRepository Repository for standard CRUD operations with user entities.
     * @param mongoTemplate  MongoDB template to execute queries that need advanced customization.
     */
    public UserServiceImpl(UserRepository userRepository, MongoTemplate mongoTemplate) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Saves a new user to the database and returns an API response indicating the result.
     *
     * @param user User data to be saved.
     * @return ApiResponse object encapsulating the status and message of the operation.
     */
    @Override
    public ApiResponse save(UserDtoL user) {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(user.firstName());
        userEntity.setLastName(user.lastName());
        userEntity.setEmail(user.email());
        userEntity.setUsername(user.username());
        userEntity.setPassword(user.password());
        userRepository.save(userEntity);
        return new ApiResponse(201, Message.USER_SAVE_SUCCESSFULLY, HttpStatus.CREATED, LocalDateTime.now());
    }

    /**
     * Updates a user by username and returns the result as an API response.
     *
     * @param username The username of the user to update.
     * @param userDto New user data for the update.
     * @return ApiResponse indicating success or failure of the update.
     */
    @Override
    public ApiResponse updateUser(String username, UserDto userDto) {

        // Verify if the user exists
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(Message.USER_NOT_FOUND + " " + username));

        // Verify if the username or email already exists
        if (userRepository.existsByUsername(userDto.getUsername())) {
            throw new IllegalArgumentException(Message.USERNAME_TAKEN);
        }
        if (userRepository.existsByEmail(userDto.getEmail())) {
            throw new IllegalArgumentException(Message.EMAIL_TAKEN);
        }

        // Update user
        user.setUsername(userDto.getUsername());
        user.setFirstName(userDto.getFirstName());
        user.setLastName(userDto.getLastName());
        user.setEmail(userDto.getEmail());

        // Save user
        UserEntity updateUser = userRepository.save(user);

        return new ApiResponse(200, Message.USER_UPDATE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());
    }

    /**
     * Finds a user by username and returns their data as a DTO.
     *
     * @param username The username of the user to find.
     * @return UserDto containing the user's data.
     */
    @Override
    public UserDto findByUsername(String username) {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException(Message.USER_NOT_FOUND + " " + username));
        return UserMapper.toDto(userEntity);
    }

    /**
     * Deletes a user by username and returns an API response to indicate the operation's status.
     *
     * @param username The username of the user to be deleted.
     * @return ApiResponse indicating success or failure of the deletion.
     */
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

    /**
     * Finds all users based on filters and pagination parameters and returns the results paginated.
     *
     * @param username Optional filter by username.
     * @param lastname Optional filter by last name.
     * @param firstname Optional filter by first name.
     * @param email Optional filter by email.
     * @param pageable Pagination parameters.
     * @return A page of UserDto objects corresponding to the filtered and paginated results.
     */
    @Override
    public Page<UserDto> findAllUsers(String username, String lastname, String firstname, String email, Pageable pageable) {
        Criteria criteria = new Criteria();

        // Agregar filtros solo si no son nulos o vacíos
        if (username != null && !username.isEmpty()) {
            criteria.and("username").is(username);
        }
        if (lastname != null && !lastname.isEmpty()) {
            criteria.and("lastname").is(lastname);
        }
        if (firstname != null && !firstname.isEmpty()) {
            criteria.and("firstname").is(firstname);
        }
        if (email != null && !email.isEmpty()) {
            criteria.and("email").is(email);
        }

        Page<UserEntity> userPage = userRepository.findAll(pageable);

        return userPage.map(UserMapper::toDto);
    }

}
