package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.infrastructure.web.dto.UserDto;
import com.dacm.hexagonal.infrastructure.web.dto.UserDtoL;
import com.dacm.hexagonal.common.ApiResponse;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface UserService {

    ApiResponse save (UserDtoL user);

    UserDto findByUsername(String username);

    ApiResponse deleteByUsername(String username);


}
