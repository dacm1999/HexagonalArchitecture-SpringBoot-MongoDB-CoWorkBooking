package com.dacm.hexagonal.infrastructure.web.controller;

import com.dacm.hexagonal.application.port.in.SpaceService;
import com.dacm.hexagonal.application.port.out.SpaceRepository;
import com.dacm.hexagonal.infrastructure.persistence.entity.SpaceEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Space Controller
 * This class is responsible for handling the space requests
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


}
