package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.application.port.out.SpaceRepository;
import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.web.dto.SpaceRecord;
import com.dacm.hexagonal.infrastructure.web.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.web.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;

    @Autowired
    public SpaceServiceImpl(SpaceRepository spaceRepository) {
        this.spaceRepository = spaceRepository;
    }


    @Override
    public ApiResponse save(SpaceRecord space) {
        return null;
    }

    @Override
    public AddedResponse saveMultipleSpaces(SpaceEntity[] spaces) {
        return null;
    }

    @Override
    public ApiResponse updateSpace(String spaceName, SpaceRecord spaceRecord) {
        return null;
    }

    @Override
    public ApiResponse deleteBySpaceName(String spaceName) {
        return null;
    }

    @Override
    public SpaceRecord findBySpaceName(String spaceName) {
        return null;
    }

    @Override
    public Page<SpaceRecord> findAllSpaces(String spaceName, String description, String location, String capacity, boolean available, Page pageable) {
        return null;
    }

    @Override
    public List<String> getAllSpaceNames() {
        return List.of();
    }
}
