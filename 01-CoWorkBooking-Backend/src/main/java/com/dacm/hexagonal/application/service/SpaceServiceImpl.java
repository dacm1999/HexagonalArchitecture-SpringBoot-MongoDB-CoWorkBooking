package com.dacm.hexagonal.application.service;

import com.dacm.hexagonal.domain.model.Space;
import com.dacm.hexagonal.infrastructure.adapters.input.mapper.SpaceMapper;
import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.repository.SpaceRepository;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.infrastructure.adapters.output.persistence.entity.SpaceEntity;
import com.dacm.hexagonal.domain.model.dto.SpaceDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.AddedResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.ApiResponse;
import com.dacm.hexagonal.infrastructure.adapters.input.response.SpaceErrorResponse;
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

/**
 * Service class for managing spaces.
 * <p>
 * This class handles business logic related to space entities. It interacts
 * with the MongoDB database through the {@link SpaceRepository} and {@link MongoTemplate}.
 */
@Service
public class SpaceServiceImpl implements SpaceService {

    private final SpaceRepository spaceRepository;
    private final MongoTemplate mongoTemplate;

    /**
     * Constructs a new SpaceServiceImpl with necessary dependencies.
     *
     * @param spaceRepository the repository for space data interaction
     * @param mongoTemplate   the MongoDB operations template
     */
    @Autowired
    public SpaceServiceImpl(SpaceRepository spaceRepository, MongoTemplate mongoTemplate) {
        this.spaceRepository = spaceRepository;
        this.mongoTemplate = mongoTemplate;
    }

    /**
     * Saves a new space record to the database.
     * <p>
     * This method converts a {@link SpaceDto} DTO to a {@link SpaceEntity} which is then saved
     * into the database using the {@link SpaceRepository}. After the save operation, it returns
     * an {@ApiResponse} indicating the success of the operation.
     *
     * @param space the {@link SpaceDto} DTO containing the data to be saved. This data includes
     *              identifiers, names, descriptions, capacity, amenities, availability status, and location.
     * @return an {@ApiResponse} containing the status code, message, HTTP status, and the timestamp
     * of the operation. It returns HTTP status 200 and a success message if the operation is successful.
     */
    @Override
    public ApiResponse save(Space space) {
        if (spaceRepository.findBySpaceId(space.getSpaceId()).isPresent()) {
            return new ApiResponse(400, Message.SPACE_ID_ALREADY_EXISTS, HttpStatus.BAD_REQUEST, LocalDateTime.now());
        }
        SpaceEntity spaceEntity = SpaceMapper.modelToEntity(space);
        SpaceDto spaceDto = SpaceMapper.entityToDto(spaceEntity);
        spaceRepository.save(spaceEntity);
        return new ApiResponse(200, Message.SPACE_SAVE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now(), spaceDto);
    }

    /**
     * Saves multiple space entities to the database.
     * <p>
     * Processes an array of {@link SpaceEntity}, attempting to save each to the database. It checks
     * if the space ID already exists to prevent duplicates. The method accumulates successful saves
     * and errors, returning detailed information about the results of these operations.
     *
     * @param spaces the array of {@link SpaceEntity} to be saved
     * @return {@link AddedResponse} containing details of operation results, including success status,
     * total number of attempts, count of successes, failures, and lists of added spaces and failed attempts.
     */
    @Override
    public AddedResponse saveMultipleSpaces(Space[] spaces) {
        List<SpaceDto> addedSpaces = new ArrayList<>();
        List<SpaceErrorResponse> failedAddSpaces = new ArrayList<>();
        String errorDescription = "";

        List<String> spaceNames = getAllSpaceNames();

        for (Space space : spaces) {
            String spaceId = space.getSpaceId();
            errorDescription = "Space ID already exists";
            if (spaceNames.contains(spaceId)) {
                failedAddSpaces.add(new SpaceErrorResponse(spaceId, errorDescription));
                continue;
            }
            SpaceEntity spaceEntity = SpaceMapper.modelToEntity(space);
            spaceRepository.save(spaceEntity);
            addedSpaces.add(SpaceMapper.entityToDto(spaceEntity));
        }
        boolean success = !addedSpaces.isEmpty();

        AddedResponse response = new AddedResponse(success, spaces.length, addedSpaces.size(), failedAddSpaces.size(), (ArrayList) addedSpaces, (ArrayList) failedAddSpaces);
        return ResponseEntity.ok(response).getBody();
    }

    /**
     * Updates an existing space in the database based on the provided space ID and space record.
     * <p>
     * This method first retrieves the existing space by space ID. If not found, it throws an exception.
     * It checks if the new space ID from the provided space record is already in use and returns an error if so.
     * Otherwise, it updates the space entity with new values and saves it back to the repository.
     *
     * @param spaceId     the ID of the space to update
     * @param spaceRecord the new space details to update
     * @return {@link ApiResponse} indicating the result of the operation. It returns a success message and status
     * code 200 if the update is successful, or an error message and status code 400 if the space ID is already in use.
     */
    @Override
    public ApiResponse updateSpace(String spaceId, SpaceDto spaceRecord) {

        SpaceEntity spaceEntity = spaceRepository.findBySpaceId(spaceId).orElseThrow(
                () -> new IllegalArgumentException(Message.SPACE_NOT_FOUND + " " + spaceId)
        );

        //verity if the spaceId is already in use
        if (!spaceId.equals(spaceRecord.spaceName())) {
            if (spaceRepository.findBySpaceId(spaceRecord.spaceId()).isPresent()) {
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
     * Deletes a space entity from the database based on the provided space ID.
     * <p>
     * Retrieves the space entity by its ID and deletes it. If no space is found with the provided ID,
     * throws an {@link IllegalArgumentException}. This ensures that only existing spaces can be deleted.
     *
     * @param spaceId the ID of the space to be deleted
     * @return {@link ApiResponse} indicating the result of the deletion operation. Returns a success message
     * and status code 200 if the deletion is successful. If the space is not found, an exception
     * is thrown and should be handled appropriately.
     */
    @Override
    public ApiResponse deleteBySpaceId(String spaceId) {
        SpaceEntity space = spaceRepository.findBySpaceId(spaceId).orElseThrow(
                () -> new IllegalArgumentException(Message.SPACE_NOT_FOUND + " " + spaceId)
        );
        SpaceDto spaceDto = SpaceMapper.entityToDto(space);
        spaceRepository.delete(space);
        return new ApiResponse(200, Message.SPACE_DELETE_SUCCESSFULLY, HttpStatus.OK, LocalDateTime.now(), spaceDto);
    }

    /**
     * Retrieves a space record by its space ID.
     * <p>
     * This method searches for a space entity using the provided space ID. If found, it converts the entity
     * to a DTO using {@link SpaceMapper}. If no entity is found, it throws an IllegalArgumentException.
     *
     * @param spaceId the ID of the space to retrieve
     * @return {@link SpaceDto} the DTO representation of the space entity
     */
    @Override
    public SpaceDto findBySpaceId(String spaceId) {
        SpaceEntity spaceEntity = spaceRepository.findBySpaceId(spaceId).orElseThrow(
                () -> new IllegalArgumentException(Message.SPACE_NOT_FOUND + " " + spaceId)
        );
        return SpaceMapper.entityToDto(spaceEntity);
    }

    /**
     * Retrieves a paginated list of all spaces matching the given filters.
     * <p>
     * Applies filters for space name, description, location, and capacity if provided.
     * The results are returned in a paginated format.
     *
     * @param spaceName   the filter for the space name
     * @param description the filter for the space description
     * @param location    the filter for the space location
     * @param capacity    the filter for the space capacity, interpreted as a string
     * @param pageable    the pagination information
     * @return a paginated {@link Page} of {@link SpaceDto} matching the criteria
     */
    @Override
    public Page<SpaceDto> findAllSpaces(String spaceName, String description, String location, String capacity, Pageable pageable) {
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

        List<SpaceDto> records = spaces.stream()
                .map(SpaceMapper::entityToDto)
                .collect(Collectors.toList());

        return new PageImpl<>(records, pageable, total);
    }

    /**
     * Retrieves a paginated list of available spaces matching the given filters.
     * <p>
     * Filters for available spaces based on space name, description, location, and capacity.
     * Only includes spaces that are currently marked as available.
     *
     * @param spaceId     optional filter for space ID (currently not used in filtering)
     * @param spaceName   the filter for the space name
     * @param description the filter for the space description
     * @param available   boolean flag to include only available spaces (true) - currently hardcoded
     * @param location    the filter for the space location
     * @param capacity    the filter for the space capacity, interpreted as a string
     * @param pageable    the pagination information
     * @return a paginated {@link Page} of {@link SpaceDto} for available spaces
     */
    @Override
    public Page<SpaceDto> findAvailableSpaces(String spaceId, String spaceName, String description, boolean available, String location, String capacity, Pageable pageable) {
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

        List<SpaceDto> records = spaces.stream().map(SpaceMapper::entityToDto).collect(Collectors.toList());

        return new PageImpl<>(records, pageable, total);
    }


    /**
     * Retrieves a paginated list of unavailable spaces matching the given filters.
     * <p>
     * Filters for unavailable spaces based on space ID, name, description, location, and capacity.
     * Only includes spaces that are currently marked as unavailable.
     *
     * @param spaceId     optional filter for space ID (should be spaceName for correction)
     * @param spaceName   the filter for the space name
     * @param description the filter for the space description
     * @param available   boolean flag to include only unavailable spaces (false) - currently hardcoded
     * @param location    the filter for the space location
     * @param capacity    the filter for the space capacity, interpreted as a string
     * @param pageable    the pagination information
     * @return a paginated {@link Page} of {@link SpaceDto} for unavailable spaces
     */
    @Override
    public Page<SpaceDto> getUnAvailableSpaces(String spaceId, String spaceName, String description, boolean available, String location, String capacity, Pageable pageable) {
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

        List<SpaceDto> records = spaces.stream().map(SpaceMapper::entityToDto).collect(Collectors.toList());

        return new PageImpl<>(records, pageable, total);
    }

    /**
     * Retrieves a list of all unique space IDs from the database.
     * <p>
     * This method fetches all space entities, extracts their IDs, and returns a list of unique IDs.
     * It uses a HashSet to ensure that all IDs in the returned list are distinct.
     *
     * @return a List of unique space IDs currently stored in the database
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

    /**
     * Changes the availability status of a given space entity and saves the update to the database.
     * <p>
     * This method sets the availability of the specified space to the provided boolean value, then
     * persists the updated space entity to the repository.
     *
     * @param space     the space entity whose availability is to be changed
     * @param available the new availability status to set (true for available, false for unavailable)
     */
    @Override
    public void changeSpaceAvailability(SpaceEntity space, boolean available) {
        space.setAvailable(available);
        spaceRepository.save(space);
    }
}
