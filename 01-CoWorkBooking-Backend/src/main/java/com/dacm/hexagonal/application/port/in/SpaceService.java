package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.domain.model.Space;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.domain.model.dto.SpaceDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Interface for the Space Service.
 * <p>
 * This service layer interface manages all operations related to space entities.
 * It provides methods to create, update, delete, and query space records. It also includes
 * functionality to handle bulk operations and availability changes of spaces.
 */
@Service
public interface SpaceService {

    ApiResponse save(Space space);

    AddedResponse saveMultipleSpaces(Space[] spaces);

    ApiResponse updateSpace(String spaceName, SpaceDto spaceRecord);

    ApiResponse deleteBySpaceId(String spaceName);

    SpaceDto findBySpaceId(String spaceName);

    Page<SpaceDto> findAllSpaces(String spaceName, String description, String location, String capacity, Pageable pageable);

    Page<SpaceDto> findAvailableSpaces(String spaceId, String spaceName, String description, boolean available, String location, String capacity, Pageable pageable);

    Page<SpaceDto> getUnAvailableSpaces(String spaceId, String spaceName, String description, boolean available, String location, String capacity, Pageable pageable);

    List<String> getAllSpaceNames();

    void changeSpaceAvailability(SpaceEntity space, boolean b);
}