package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.common.ApiResponse;
import com.dacm.hexagonal.domain.model.Login;
import com.dacm.hexagonal.infrastructure.web.security.jwt.JwtResponse;

public interface LoginService {

    JwtResponse login(Login request);


}
