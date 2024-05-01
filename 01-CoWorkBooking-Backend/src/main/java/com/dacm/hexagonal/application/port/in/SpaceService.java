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
 * Space Service
 * This class is responsible for handling the space requests
 */
@Service
public interface SpaceService {

    ApiResponse save (SpaceRecord space);
    AddedResponse saveMultipleSpaces (SpaceEntity[] spaces);
    ApiResponse updateSpace(String spaceName, SpaceRecord spaceRecord);
    ApiResponse deleteBySpaceId(String spaceName);
    SpaceRecord findBySpaceName(String spaceName);
    Page<SpaceRecord> findAllSpaces(String spaceName, String description, String location, String capacity , Pageable pageable);
    List<String> getAllSpaceNames();
}
