package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.domain.model.Space;
import com.dacm.hexagonal.domain.model.dto.SpaceDto;
import com.dacm.hexagonal.infrastructure.adapters.input.mapper.SpaceMapper;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.SpacePaginationResponse;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.awt.print.Pageable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class SpaceControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    private SpaceService spaceService;

    @Autowired
    WebApplicationContext webApplicationContext;

    private SpaceController spaceController;

    private static String BASE_URL = "/api/v1/spaces";

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        spaceController = new SpaceController(spaceService);
        spaceService = mock(SpaceService.class);
    }

    @Test
    @DisplayName("Test createSpace method")
    void createSpace() throws Exception {

        Space space = Space.builder()
                .spaceId("1")
                .spaceName("Space 1")
                .description("Space 1 description")
                .capacity(10)
                .amenities(null)
                .available(true)
                .location("Location 1")
                .build();

        ApiResponse apiResponse = new ApiResponse(200, Message.SPACE_SAVE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());

        when(spaceService.save(space)).thenReturn(apiResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(space)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Test createMultipleSpaces method")
    void createMultipleSpaces() throws Exception {
        Space[] spaces = new Space[2];
        spaces[0] = Space.builder()
                .spaceId("1")
                .spaceName("Space 1")
                .location("Location 1")
                .build();
        spaces[1] = Space.builder()
                .spaceId("2")
                .spaceName("Space 2")
                .location("Location 2")
                .build();

        ApiResponse apiResponse = new ApiResponse(200, Message.SPACE_SAVE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());

        when(spaceService.save(ArgumentMatchers.any(Space.class))).thenReturn(apiResponse);

        mockMvc.perform(MockMvcRequestBuilders.post(BASE_URL + "/createMultiple")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(spaces)))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Test findSpaceById method")
    void findSpaceById() throws Exception {
        String spaceId = "1";
        Space space = Space.builder()
                .spaceId("1")
                .spaceName("Space 1")
                .location("Location 1")
                .build();


        when(spaceService.findBySpaceId(spaceId)).thenReturn(SpaceMapper.modelToDto(space));

        mockMvc.perform(get(BASE_URL + "/find/" + spaceId))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Test findAllSpaces method")
    void findAllSpaces() throws Exception {
        // Given
        int page = 0;
        int size = 10;

        // When
        MvcResult result = mockMvc.perform(get(BASE_URL + "/all")
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size))
                        .accept(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNotFound())
                .andReturn();

        // Then
        String content = result.getResponse().getContentAsString();
        assertNotNull(content);

        SpacePaginationResponse response = objectMapper.readValue(content, SpacePaginationResponse.class);
        assertNotNull(response);
    }

    @Test
    @DisplayName("Test delete method")
    void deleteSpace() throws Exception {

        String spaceId = "1";
        ApiResponse apiResponse = new ApiResponse(200, Message.SPACE_DELETE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());

        when(spaceService.deleteBySpaceId(spaceId)).thenReturn(apiResponse);

        mockMvc.perform(MockMvcRequestBuilders.delete(BASE_URL + "/delete/" + spaceId))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    @DisplayName("Test updateSpace method")
    void updateSpace() throws Exception {

        String spaceId = "1";
        Space space = Space.builder()
                .spaceId("1")
                .spaceName("Space 1")
                .location("Location 1")
                .build();

        ApiResponse apiResponse = new ApiResponse(200, Message.SPACE_UPDATE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());

        when(spaceService.updateSpace(spaceId, SpaceMapper.modelToDto(space))).thenReturn(apiResponse);

        mockMvc.perform(MockMvcRequestBuilders.put(BASE_URL + "/update/" + spaceId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(space)))
                .andExpect(status().isOk())
                .andReturn();
    }

    private SpacePaginationResponse createSpacePaginationResponse(List<SpaceDto> spaceDtoList, long totalElements) {
        SpacePaginationResponse response = new SpacePaginationResponse();
        response.setSpaces(spaceDtoList);
        response.setTotalPages(1);
        response.setTotalElements(totalElements);
        response.setNumber(0);
        response.setNumberOfElements(spaceDtoList.size());
        response.setSize(spaceDtoList.size());
        return response;
    }
}