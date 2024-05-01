package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.application.mapper.SpaceMapper;
import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.application.port.out.SpaceRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.infrastructure.web.dto.SpaceRecord;
import com.dacm.hexagonal.infrastructure.web.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.web.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.web.response.SpaceErrorResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
                .spaceId(space.spaceId())
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
        List<SpaceRecord> addedSpaces = new ArrayList<>();
        List<SpaceErrorResponse> spacesAddFailed = new ArrayList<>();
        String errorDescription = "";

        List<String> spaceNames = getAllSpaceNames();

        for(SpaceEntity space : spaces){
            String spaceId = space.getSpaceId();
            errorDescription = "Space ID already exists";
            if(spaceNames.contains(spaceId)) {
                spacesAddFailed.add(new SpaceErrorResponse(spaceId, errorDescription));
                continue;
            }
            SpaceEntity spaceEntity = SpaceEntity.builder()
                    .spaceId(space.getSpaceId())
                    .spaceName(space.getSpaceName())
                    .description(space.getDescription())
                    .capacity(space.getCapacity())
                    .amenities(space.getAmenities())
                    .available(space.isAvailable())
                    .location(space.getLocation())
                    .build();
            spaceRepository.save(spaceEntity);
            addedSpaces.add(SpaceMapper.toDto(spaceEntity));
        }
        int total = spaces.length;
        int success = addedSpaces.size();
        int failed = spacesAddFailed.size();
        boolean sucess = success > 0;

        AddedResponse response = new AddedResponse(sucess, total, success, failed, (ArrayList) addedSpaces, (ArrayList) spacesAddFailed);
        return ResponseEntity.ok(response).getBody();
    }

    /**
     * @param spaceId
     * @param spaceRecord
     * @return
     */
    @Override
    public ApiResponse updateSpace(String spaceId, SpaceRecord spaceRecord) {

        SpaceEntity spaceEntity = spaceRepository.findBySpaceId(spaceId).orElseThrow(
                () -> new IllegalArgumentException(Message.SPACE_NOT_FOUND + " " + spaceId)
        );

        //verity if the spaceId is already in use
        if(!spaceId.equals(spaceRecord.spaceName())){
            if(spaceRepository.findBySpaceId(spaceRecord.spaceId()).isPresent()){
                return new ApiResponse(400, Message.SPACE_ID_ALREADY_EXISTS, HttpStatus.BAD_REQUEST, LocalDateTime.now());
            }
        }
        spaceEntity.setSpaceId(spaceRecord.spaceId());
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
     * @param spaceId
     * @return
     */
    @Override
    public ApiResponse deleteBySpaceId(String spaceId) {
        SpaceEntity space = spaceRepository.findBySpaceId(spaceId).orElseThrow(
                () -> new IllegalArgumentException(Message.SPACE_NOT_FOUND + " " + spaceId)
        );
        spaceRepository.delete(space);

        return new ApiResponse(200, Message.SPACE_DELETE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now());
    }

    /**
     * @param spaceId
     * @return
     */
    @Override
    public SpaceRecord findBySpaceName(String spaceId) {
        SpaceEntity spaceEntity = spaceRepository.findBySpaceId(spaceId).orElseThrow(
                () -> new IllegalArgumentException(Message.SPACE_NOT_FOUND + " " + spaceId)
        );
        return SpaceMapper.toDto(spaceEntity);
    }

    /**
     * @param spaceName
     * @param description
     * @param location
     * @param capacity
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

    @Override
    public Page<SpaceRecord> findAvailableSpaces(String spaceId,String spaceName, String description, boolean available, String location, String capacity, Pageable pageable) {
        Criteria criteria = new Criteria("available").is(true);

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

//        criteria = criteria.and("available").is(available);

        Query query = Query.query(criteria).with(pageable);
        List<SpaceEntity> spaces = mongoTemplate.find(query, SpaceEntity.class);
        long total = mongoTemplate.count(query.limit(-1).skip(-1), SpaceEntity.class);

        List<SpaceRecord> records = spaces.stream().map(SpaceMapper::toDto).collect(Collectors.toList());

        return new PageImpl<>(records, pageable, total);
    }


    @Override
    public Page<SpaceRecord> getUnAvailableSpaces(String spaceId,String spaceName, String description,boolean available ,String location, String capacity, Pageable pageable) {
        Criteria criteria = new Criteria();

        if (spaceId != null && !spaceId.isEmpty()) {
            criteria = Criteria.where("spaceId").is(spaceName);
        }
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

        criteria = criteria.and("available").is(false);

        Query query = Query.query(criteria).with(pageable);
        List<SpaceEntity> spaces = mongoTemplate.find(query, SpaceEntity.class);
        long total = mongoTemplate.count(query.limit(-1).skip(-1), SpaceEntity.class);

        List<SpaceRecord> records = spaces.stream().map(SpaceMapper::toDto).collect(Collectors.toList());

        return new PageImpl<>(records, pageable, total);
    }

    /**
     * @return
     */
    @Override
    public List<String> getAllSpaceNames() {
        HashSet<SpaceEntity> spaces = new HashSet<>(spaceRepository.findAll());
        HashSet<String> spacesId =
                spaces.stream().
                        map(SpaceEntity::getSpaceId).
                        collect(HashSet::new, HashSet::add, HashSet::addAll);
        return List.copyOf(spacesId);
    }

    @Override
    public void changeSpaceAvailability(SpaceEntity space, boolean available) {
        space.setAvailable(available);
        spaceRepository.save(space);
    }
}
