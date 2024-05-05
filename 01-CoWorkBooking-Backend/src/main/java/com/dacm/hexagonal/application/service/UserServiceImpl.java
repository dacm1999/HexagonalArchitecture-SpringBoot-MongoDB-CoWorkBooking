package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.domain.model.User;
import com.dacm.hexagonal.infrastructure.adapters.input.mapper.UserMapper;
import com.dacm.hexagonal.application.port.in.UserService;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.domain.enums.Role;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.UserEntity;
import com.dacm.hexagonal.domain.model.dto.UserDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.UserErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    private PasswordEncoder passwordEncoder;

    /**
     * Constructor for injecting necessary dependencies for repository operations and MongoDB.
     *
     * @param userRepository  Repository for standard CRUD operations with user entities.
     * @param mongoTemplate   MongoDB template to execute queries that need advanced customization.
     * @param passwordEncoder Component used for encoding passwords securely.
     */
    public UserServiceImpl(UserRepository userRepository, MongoTemplate mongoTemplate, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Saves a new user to the database and returns an API response indicating the result.
     *
     * @param user User data to be saved.
     * @return ApiResponse object encapsulating the status and message of the operation.
     */
    @Override
    public ApiResponse save(User user) {
        UserEntity userEntity = UserEntity.builder()
                .userId(user.getUserId())
                .password(passwordEncoder.encode(user.getPassword()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(Role.ROLE_USER)
                .email(user.getEmail())
                .build();
        userRepository.save(userEntity);
        return new ApiResponse(201, Message.USER_SAVE_SUCCESSFULLY, HttpStatus.CREATED, LocalDateTime.now());
    }

    /**
     * Saves multiple user entities to the database while checking for duplicates in usernames and emails.
     * This method iterates over an array of UserEntity objects, checks for existing usernames and emails,
     * and processes each user accordingly. If the userId or email already exists, the user is not added,
     * and an error report is generated. Successful additions are tracked and returned in the response.
     *
     * @param users An array of UserEntity objects to be saved.
     * @return An AddedResponse object that includes details about the success of the operation,
     * the total number of users attempted to be added, the number of successful additions,
     * the number of failures, lists of successfully added users and failed users, and a reason for any failures.
     */
    @Override
    public AddedResponse saveMultipleUsers(User[] users) {
        List<UserDto> addedUsers = new ArrayList<>();
        List<UserErrorResponse> usersFailed = new ArrayList<>();
        String errorDescription = "";

        List<String> usernames = getAllUsernames();
        List<String> emails = getAllEmails();

        Set<String> existingUsernames = new HashSet<>(usernames);
        Set<String> existingEmails = new HashSet<>(emails);

        for (User user : users) {
            String username = user.getUserId();
            String email = user.getEmail();

            errorDescription = "Username duplicated";

            if (existingUsernames.contains(username)) {
                usersFailed.add(new UserErrorResponse(username, email, errorDescription));
                continue;
            }

            if (existingEmails.contains(email)) {
                errorDescription = "Email duplicated";
                usersFailed.add(new UserErrorResponse(username, email, errorDescription));
                continue;
            }

            UserEntity userEntity = UserEntity.builder()
                    .userId(username)
                    .password(passwordEncoder.encode(user.getPassword()))
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .role(Role.ROLE_USER)
                    .email(email)
                    .build();

            userRepository.save(userEntity);

            existingUsernames.add(username);
            existingEmails.add(email);

            addedUsers.add(UserMapper.domainToDto(user));
        }

        boolean success = !addedUsers.isEmpty();

        AddedResponse result = new AddedResponse(success, users.length, addedUsers.size(), usersFailed.size(), (ArrayList) addedUsers, (ArrayList) usersFailed);

        return ResponseEntity.ok(result).getBody();
    }

    /**
     * Updates a user by userId and returns the result as an API response.
     *
     * @param userId The userId of the user to update.
     * @param user  New user data for the update.
     * @return ApiResponse indicating success or failure of the update.
     */
    @Override
    public ApiResponse updateUser(String userId, User user) {

        // Verify if the user exists
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            throw new UsernameNotFoundException(Message.USER_NOT_FOUND + " " + userId);
        }

        // Verify if the userId or email already exists
        if (userRepository.existsByUserId(user.getUserId())) {
            throw new IllegalArgumentException(Message.USERNAME_TAKEN);
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new IllegalArgumentException(Message.EMAIL_TAKEN);
        }

        // Update user data
        userEntity.setUserId(user.getUserId());
        userEntity.setFirstName(user.getFirstName());
        userEntity.setLastName(user.getLastName());
        userEntity.setEmail(user.getEmail());

        // Save user
        UserEntity updateUser = userRepository.save(userEntity);

        return new ApiResponse(200, Message.USER_UPDATE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());
    }

    /**
     * Finds a user by userId and returns their data as a DTO.
     *
     * @param userId The userId of the user to find.
     * @return UserDto containing the user's data.
     */
    @Override
    public UserDto findByUserId(String userId) {
        UserEntity userEntity = userRepository.findByUserId(userId);
        if (userEntity == null) {
            return null;
        }
        return UserMapper.entityToDto(userEntity);
    }

    /**
     * Deletes a user by userId and returns an API response to indicate the operation's status.
     *
     * @param userId The userId of the user to be deleted.
     * @return ApiResponse indicating success or failure of the deletion.
     */
    @Override
    public ApiResponse deleteByUserId(String userId) {
        UserEntity user = userRepository.findByUserId(userId);
        if(user == null) {
            throw new UsernameNotFoundException(Message.USER_NOT_FOUND + " " + userId);
        }
        userRepository.deleteByUserId(userId);
        UserDto userDto = UserMapper.entityToDto(user);
        return new ApiResponse(200, Message.USER_DELETE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now(), userDto);
    }

    /**
     * Finds all users based on filters and pagination parameters and returns the results paginated.
     *
     * @param userId  Optional filter by userId.
     * @param lastName  Optional filter by last name.
     * @param firstName Optional filter by first name.
     * @param email     Optional filter by email.
     * @param pageable  Pagination parameters.
     * @return A page of UserDto objects corresponding to the filtered and paginated results.
     */
    @Override
    public Page<UserDto> findAllUsers(String userId, String lastName, String firstName, String email, Pageable pageable) {
        Criteria criteria = new Criteria();

        // Agregar filtros solo si no son nulos o vacíos
        if (userId != null && !userId.isEmpty()) {
            criteria.and("userId").is(userId);
        }
        if (lastName != null && !lastName.isEmpty()) {
            criteria.and("lastName").is(lastName);
        }
        if (firstName != null && !firstName.isEmpty()) {
            criteria.and("firstName").is(firstName);
        }
        if (email != null && !email.isEmpty()) {
            criteria.and("email").is(email);
        }

        Query query = Query.query(criteria).with(pageable);
        List<UserEntity> spaces = mongoTemplate.find(query, UserEntity.class);
        long total = mongoTemplate.count(Query.query(criteria), UserEntity.class);

        List<UserDto> records = spaces.stream()
                .map(UserMapper::entityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(records, pageable, total);
    }

    @Override
    public List<String> getAllUsernames() {
        List<UserEntity> users = userRepository.findAll();
        List<String> usernames = users.stream()
                .map(UserEntity::getUserId)
                .collect(Collectors.toList());
        return usernames;
    }

    @Override
    public List<String> getAllEmails() {
        List<UserEntity> users = userRepository.findAll();
        List<String> emails = users.stream()
                .map(UserEntity::getEmail)
                .collect(Collectors.toList());
        return emails;
    }

}
