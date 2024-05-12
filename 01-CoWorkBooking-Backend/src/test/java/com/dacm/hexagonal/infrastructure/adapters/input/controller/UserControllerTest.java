package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.domain.model.User;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@WebAppConfiguration
class UserControllerTest {

    private final static String BASE_URL = "/api/v1/users";
    MockMvc mockMvc;

    @Autowired
    WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
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
    void findByUserID() {
    }

    @Test
    void showAllUsers() throws Exception {

        MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.get(BASE_URL + "/allUsers")
                        .queryParam("page", "0")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();

        assertEquals(200, mockMvcResult.getResponse().getStatus());
    }

    @Test
    void deleteByUserId() throws Exception {
        MvcResult mockMvcResult = mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/delete/{userId}", "dacn")
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andReturn();
        assertEquals(200, mockMvcResult.getResponse().getStatus());
    }

    @Test
    void updateByUsername() {
    }


    private User buildUser(){
        return User.builder()
                .firstName("Daniel")
                .lastName("Contreras")
                .email("dacm.dev@icloud.com")
                .userId("dacm")
                .password("1234").build();
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