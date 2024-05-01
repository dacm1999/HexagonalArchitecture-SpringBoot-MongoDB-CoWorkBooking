package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.web.dto.SpaceRecord;
import com.dacm.hexagonal.infrastructure.web.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.web.response.ApiResponse;
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

    ApiResponse save(SpaceRecord space);

    AddedResponse saveMultipleSpaces(SpaceEntity[] spaces);

    ApiResponse updateSpace(String spaceName, SpaceRecord spaceRecord);

    ApiResponse deleteBySpaceId(String spaceName);

    SpaceRecord findBySpaceId(String spaceName);

    Page<SpaceRecord> findAllSpaces(String spaceName, String description, String location, String capacity, Pageable pageable);

    Page<SpaceRecord> findAvailableSpaces(String spaceId, String spaceName, String description, boolean available, String location, String capacity, Pageable pageable);

    Page<SpaceRecord> getUnAvailableSpaces(String spaceId, String spaceName, String description, boolean available, String location, String capacity, Pageable pageable);

    List<String> getAllSpaceNames();

    void changeSpaceAvailability(SpaceEntity space, boolean b);
}