package com.dacm.hexagonal.infrastructure.web.controller;

import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.application.port.out.SpaceRepository;
import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.web.dto.SpaceRecord;
import com.dacm.hexagonal.infrastructure.web.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.web.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.web.response.SpacePaginationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Space Controller
 * This class is responsible for handling the space requests
 *
 * @version 1.0
 * @see SpaceService
 * @see SpaceRepository
 * @see SpaceEntity
 */
@RestController
@RequestMapping("/api/v1/spaces")
public class SpaceController {

    private final SpaceService spaceService;

    @Autowired
    public SpaceController(SpaceService spaceService) {
        this.spaceService = spaceService;
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponse> createProduct(@RequestBody SpaceRecord spaceDto) {
        return ResponseEntity.ok(spaceService.save(spaceDto));
    }

    @PostMapping("/createMultiple")
    public ResponseEntity<AddedResponse> createMultipleProducts(@RequestBody SpaceEntity[] spaces) {
        return ResponseEntity.ok(spaceService.saveMultipleSpaces(spaces));
    }

    @GetMapping("/find/{spaceId}")
    public ResponseEntity<SpaceRecord> findSpaceByName(@PathVariable String spaceId) {
        return ResponseEntity.ok(spaceService.findBySpaceName(spaceId));
    }

    @GetMapping("/all")
    public ResponseEntity<SpacePaginationResponse> findAllSpaces(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String spaceName,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String location,
//            @RequestParam(required = false) boolean available,
            @RequestParam(required = false) String capacity

    ) {

        Pageable pageable = PageRequest.of(page, size);
        Page<SpaceRecord> spaces = spaceService.findAllSpaces(spaceName, description, location, capacity, pageable);

        SpacePaginationResponse response = new SpacePaginationResponse();
        response.setSpaces(spaces.getContent());
        response.setTotalPages(spaces.getTotalPages());
        response.setTotalElements(spaces.getTotalElements());
        response.setNumber(spaces.getNumber());
        response.setSize(spaces.getSize());

        return ResponseEntity.ok(response);
    }

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
        Page<SpaceRecord> spaces = spaceService.findAvailableSpaces(spaceId,spaceName, description, available, location, capacity, pageable);

        SpacePaginationResponse response = new SpacePaginationResponse();
        response.setSpaces(spaces.getContent());
        response.setTotalPages(spaces.getTotalPages());
        response.setTotalElements(spaces.getTotalElements());
        response.setNumber(spaces.getNumber());
        response.setSize(spaces.getSize());

        return ResponseEntity.ok(response);
    }

    @GetMapping("/allUnAvailable")
    public ResponseEntity<SpacePaginationResponse> getUnAvailableSpaces(
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
        Page<SpaceRecord> spaces = spaceService.getUnAvailableSpaces(spaceId,spaceName, description, available, location, capacity, pageable);

        SpacePaginationResponse response = new SpacePaginationResponse();
        response.setSpaces(spaces.getContent());
        response.setTotalPages(spaces.getTotalPages());
        response.setTotalElements(spaces.getTotalElements());
        response.setNumber(spaces.getNumber());
        response.setSize(spaces.getSize());

        return ResponseEntity.ok(response);
    }


    @DeleteMapping("/delete/{spaceId}")
    public ResponseEntity<ApiResponse> deleteSpace(@PathVariable String spaceId) {
        return ResponseEntity.ok(spaceService.deleteBySpaceId(spaceId));
    }

    @PutMapping("/update/{spaceId}")
    public ResponseEntity<ApiResponse> updateSpace(@PathVariable String spaceId, @RequestBody SpaceRecord spaceRecord) {
        return ResponseEntity.ok(spaceService.updateSpace(spaceId, spaceRecord));
    }


}
