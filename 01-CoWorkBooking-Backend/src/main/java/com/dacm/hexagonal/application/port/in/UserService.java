package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.infrastructure.web.dto.UserDto;
import com.dacm.hexagonal.common.ApiResponse;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    ApiResponse save (UserDto user);
}
