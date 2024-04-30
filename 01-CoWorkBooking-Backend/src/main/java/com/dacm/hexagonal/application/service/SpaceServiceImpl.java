package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.mapper.SpaceMapper;
import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.application.port.out2.SpaceRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.web.dto.SpaceRecord;
import com.dacm.hexagonal.infrastructure.web.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.web.response.ApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;
    private final MongoTemplate mongoTemplate;

    @Autowired
    public SpaceServiceImpl(SpaceRepository spaceRepository, MongoTemplate mongoTemplate) {
        this.spaceRepository = spaceRepository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * @param space
     * @return
     */
    @Override
    public ApiResponse save(SpaceRecord space) {
        SpaceEntity spaceEntity = SpaceEntity.builder()
                .spaceName(space.spaceName())
                .description(space.description())
                .capacity(space.capacity())
                .amenities(space.amenities())
                .available(space.available())
                .location(space.location())
                .build();
        spaceRepository.save(spaceEntity);
        return new ApiResponse(200, Message.SPACE_SAVE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());
    }

    /**
     * @param spaces
     * @return
     */
    @Override
    public AddedResponse saveMultipleSpaces(SpaceEntity[] spaces) {

        return null;
    }

    /**
     * @param spaceName
     * @param spaceRecord
     * @return
     */
    @Override
    public ApiResponse updateSpace(String spaceName, SpaceRecord spaceRecord) {

        SpaceEntity spaceEntity = spaceRepository.findBySpaceName(spaceName).orElseThrow(
                () -> new IllegalArgumentException(Message.SPACE_NOT_FOUND + " " + spaceName)
        );

        spaceEntity.setSpaceName(spaceRecord.spaceName());
        spaceEntity.setDescription(spaceRecord.description());
        spaceEntity.setCapacity(spaceRecord.capacity());
        spaceEntity.setAmenities(spaceRecord.amenities());
        spaceEntity.setAvailable(spaceRecord.available());
        spaceEntity.setLocation(spaceRecord.location());

        spaceRepository.save(spaceEntity);

        return new ApiResponse(200, Message.SPACE_UPDATE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());
    }

    /**
     * @param spaceName
     * @return
     */
    @Override
    public ApiResponse deleteBySpaceName(String spaceName) {
        SpaceEntity space = spaceRepository.findBySpaceName(spaceName).orElseThrow(
                () -> new IllegalArgumentException(Message.SPACE_NOT_FOUND + " " + spaceName)
        );
        spaceRepository.delete(space);

        return new ApiResponse(200, Message.SPACE_DELETE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());
    }

    /**
     * @param spaceName
     * @return
     */
    @Override
    public SpaceRecord findBySpaceName(String spaceName) {
        SpaceEntity spaceEntity = spaceRepository.findBySpaceName(spaceName).orElseThrow(
                () -> new IllegalArgumentException(Message.SPACE_NOT_FOUND + " " + spaceName)
        );
        return SpaceMapper.toDto(spaceEntity);
    }

    /**
     * @param spaceName
     * @param description
     * @param location
     * @param capacity
     * @param available
     * @param pageable
     * @return
     */
    @Override
    public Page<SpaceRecord> findAllSpaces(String spaceName, String description, String location, String capacity, Pageable pageable) {
        Criteria criteria = new Criteria();

        if (spaceName != null && !spaceName.isEmpty()) {
            criteria = Criteria.where("spaceName").is(spaceName);
        }
        if (description != null && !description.isEmpty()) {
            criteria = criteria.and("description").is(description);
        }
        if (location != null && !location.isEmpty()) {
            criteria = criteria.and("location").is(location);
        }
        if (capacity != null && !capacity.isEmpty()) {
            criteria = criteria.and("capacity").is(capacity);
        }

        Query query = Query.query(criteria).with(pageable);
        System.out.println(query.toString());
        List<SpaceEntity> spaces = mongoTemplate.find(query, SpaceEntity.class);
        long total = mongoTemplate.count(Query.query(criteria), SpaceEntity.class);

        List<SpaceRecord> records = spaces.stream()
                .map(SpaceMapper::toDto)
                .collect(Collectors.toList());

        return new PageImpl<>(records, pageable, total);
    }

    /**
     * @return
     */
    @Override
    public List<String> getAllSpaceNames() {
        HashSet<SpaceEntity> spaces = new HashSet<>(spaceRepository.findAll());
        HashSet<String> spaceNames =
                spaces.stream().
                        map(SpaceEntity::getSpaceName).
                        collect(HashSet::new, HashSet::add, HashSet::addAll);
        return List.copyOf(spaceNames);
    }
}
