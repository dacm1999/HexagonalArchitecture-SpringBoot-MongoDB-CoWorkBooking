package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.UserService;
import com.dacm.hexagonal.domain.model.User;
import com.dacm.hexagonal.domain.model.dto.UserDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.UserPaginationResponse;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class UserControllerTest {

    private final static String BASE_URL = "/api/v1/users";

    @Autowired
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @MockBean
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    @DisplayName("Create a new user")
    void createUser() throws Exception {
        User user = buildUser();
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/create")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(user)))
                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @DisplayName("Create multiple users")
    void createMultipleUsers() throws Exception {
        User[] users = {buildUser()};
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/createMultiple")
                        .accept(MediaType.APPLICATION_JSON_VALUE)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(mapToJson(users)))
                .andReturn();
        assertEquals(200, result.getResponse().getStatus());
    }

    @Test
    @DisplayName("Find user by userId")
    void findByUserID() throws Exception {
        mockMvc.perform(get(BASE_URL + "/find/{userId}", "dacm")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Show all users")
    void showAllUsers() throws Exception {
        // Given
        int pageNumber = 0;
        int pageSize = 10;
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<UserDto> userDtoList = Collections.singletonList(new UserDto());
        long totalElements = 1;

        Page<UserPaginationResponse> userPage = new PageImpl<>(Collections.singletonList(createUserPaginationResponse(userDtoList, totalElements)), pageable, 1);
        when(userService.findAllUsers(anyString(), anyString(), anyString(), anyString(), any(Pageable.class)))
                .thenReturn(userPage);

        // When
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/allUsers")
                        .param("page", String.valueOf(pageNumber))
                        .param("size", String.valueOf(pageSize))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        // Then
        assertEquals(404, mvcResult.getResponse().getStatus());
        String content = mvcResult.getResponse().getContentAsString();
        assertNotNull(content);

    }

    @Test
    @DisplayName("Delete user by userId")
    void deleteByUserId() throws Exception {
        MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/delete/{userId}", "dacm")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        assertEquals(200, mockMvcResult.getResponse().getStatus());
    }

    @Test
    @DisplayName("Update user by userId")
    void updateByUsername() throws Exception {
        String username = "testUsername";
        User user = new User();
        user.setUserId("dacm");
        user.setFirstName("Daniel");
        user.setLastName("Contreras");
        String updatedUserJson = mapToJson(user);

        when(userService.updateUser("dacm", user)).thenReturn(new ApiResponse(HttpStatus.OK.value(), "User updated successfully", HttpStatus.OK, LocalDateTime.now()));

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/update/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON_VALUE)
                        .content(updatedUserJson))
                .andReturn();

        // Assert
        assertEquals(200, result.getResponse().getStatus());
    }

    private User buildUser() {
        return User.builder()
                .firstName("Daniel")
                .lastName("Contreras")
                .email("dacm.dev@icloud.com")
                .userId("dacm")
                .password("1234").build();
    }

    private UserPaginationResponse createUserPaginationResponse(List<UserDto> userDtoList, long totalElements) {
        UserPaginationResponse response = new UserPaginationResponse();
        response.setUsers(userDtoList);
        response.setTotalPages(1);
        response.setTotalElements(totalElements);
        response.setNumber(0);
        response.setNumberOfElements(userDtoList.size());
        response.setSize(userDtoList.size());
        return response;
    }

    private String mapToJson(Object object) {
        try {
            return new ObjectMapper().writeValueAsString(object);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }
}