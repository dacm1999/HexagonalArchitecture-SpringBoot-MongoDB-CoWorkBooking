package com.dacm.hexagonal.infrastructure.adapters.input.controller;

import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.domain.model.Space;
import com.dacm.hexagonal.domain.model.dto.SpaceDto;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.SpaceRepository;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.SpacePaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for managing space-related operations.
 * This class handles all incoming HTTP requests for creating, retrieving,
 * updating, and deleting spaces.
 *
 * @version 1.0
 * @see SpaceService The service responsible for business logic associated with spaces.
 * @see SpaceRepository The repository interface for space data persistence.
 * @see SpaceEntity The entity model for a space.
 */
@RestController
@RequestMapping("/api/v1/spaces")
public class SpaceController {

    private final SpaceService spaceService;

    /**
     * Constructs a SpaceController with necessary service dependency.
     *
     * @param spaceService The service handling space operations.
     */
    @Autowired
    public SpaceController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    /**
     * Creates a new space and returns the operation result.
     *
     * @param spaceDto The DTO of the space to create.
     * @return ResponseEntity containing the operation's ApiResponse.
     */
    @PostMapping("/create")
    public ResponseEntity<?> createSpace(@RequestBody Space spaceDto) {
        return ResponseEntity.ok(spaceService.save(spaceDto));
    }

    /**
     * Creates multiple spaces at once and returns the operation result.
     *
     * @param spaces Array of space entities to create.
     * @return ResponseEntity containing the operation's AddedResponse.
     */
    @PostMapping("/createMultiple")
    public ResponseEntity<AddedResponse> createMultipleSpaces(@RequestBody Space[] spaces) {
        return ResponseEntity.ok(spaceService.saveMultipleSpaces(spaces));
    }

    /**
     * Retrieves a specific space by its ID.
     *
     * @param spaceId The ID of the space to find.
     * @return ResponseEntity containing the found SpaceRecord.
     */
    @GetMapping("/find/{spaceId}")
    public ResponseEntity<SpaceDto> findSpaceById(@PathVariable String spaceId) {
        return ResponseEntity.ok(spaceService.findBySpaceId(spaceId));
    }

    /**
     * Fetches all spaces with optional filters and paginated results.
     *
     * @param page        Default to 0, the page number of the paginated results.
     * @param size        Default to 10, the size of the page to return.
     * @param spaceName   Optional filter by space name.
     * @param description Optional filter by description.
     * @param location    Optional filter by location.
     * @param capacity    Optional filter by capacity.
     * @return Paginated response of spaces.
     */
    @GetMapping("/all")
    public ResponseEntity<SpacePaginationResponse> findAllSpaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String spaceName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String capacity

    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SpacePaginationResponse> spaces = spaceService.findAllSpaces(spaceName, description, location, capacity, pageable);
        SpacePaginationResponse response = spaces.getContent().get(0);
        return ResponseEntity.ok(response);
    }

    /**
     * Fetches all available spaces with optional filters and paginated results.
     *
     * @param page        Default to 0, the page number of the paginated results.
     * @param size        Default to 10, the size of the page to return.
     * @param spaceId     Optional filter by space ID.
     * @param spaceName   Optional filter by space name.
     * @param description Optional filter by description.
     * @param location    Optional filter by location.
     * @param available   Filter by availability status.
     * @param capacity    Optional filter by capacity.
     * @return Paginated response of available spaces.
     */
    @GetMapping("/allAvailable")
    public ResponseEntity<SpacePaginationResponse> findAvailableSpaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String spaceId,
            @RequestParam(required = false) String spaceName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) boolean available,
            @RequestParam(required = false) String capacity

    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SpaceDto> spaces = spaceService.findAvailableSpaces(spaceId, spaceName, description, available, location, capacity, pageable);

        SpacePaginationResponse response = new SpacePaginationResponse();
        response.setSpaces(spaces.getContent());
        response.setTotalPages(spaces.getTotalPages());
        response.setTotalElements(spaces.getTotalElements());
        response.setNumber(spaces.getNumber());
        response.setSize(spaces.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * Fetches all unavailable spaces with optional filters and paginated results.
     *
     * @param page        Default to 0, the page number of the paginated results.
     * @param size        Default to 10, the size of the page to return.
     * @param spaceId     Optional filter by space ID.
     * @param spaceName   Optional filter by space name.
     * @param description Optional filter by description.
     * @param location    Optional filter by location.
     * @param available   Filter by availability status.
     * @param capacity    Optional filter by capacity.
     * @return Paginated response of available spaces.
     */
    @GetMapping("/allUnAvailable")
    public ResponseEntity<SpacePaginationResponse> findUnAvailableSpaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String spaceId,
            @RequestParam(required = false) String spaceName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) boolean available,
            @RequestParam(required = false) String capacity

    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SpaceDto> spaces = spaceService.getUnAvailableSpaces(spaceId, spaceName, description, available, location, capacity, pageable);

        SpacePaginationResponse response = new SpacePaginationResponse();
        response.setSpaces(spaces.getContent());
        response.setTotalPages(spaces.getTotalPages());
        response.setTotalElements(spaces.getTotalElements());
        response.setNumber(spaces.getNumber());
        response.setSize(spaces.getSize());

        return ResponseEntity.ok(response);
    }

    /**
     * Deletes a space by its ID.
     *
     * @param spaceId The ID of the space to delete.
     * @return ResponseEntity containing the result of the deletion operation.
     */
    @DeleteMapping("/delete/{spaceId}")
    public ResponseEntity<ApiResponse> deleteSpace(@PathVariable String spaceId) {
        return ResponseEntity.ok(spaceService.deleteBySpaceId(spaceId));
    }

    /**
     * Updates the details of an existing space.
     *
     * @param spaceId     The ID of the space to update.
     * @param spaceRecord The updated details of the space.
     * @return ResponseEntity containing the result of the update operation.
     */
    @PutMapping("/update/{spaceId}")
    public ResponseEntity<ApiResponse> updateSpace(@PathVariable String spaceId, @RequestBody SpaceDto spaceRecord) {
        return ResponseEntity.ok(spaceService.updateSpace(spaceId, spaceRecord));
    }


}
