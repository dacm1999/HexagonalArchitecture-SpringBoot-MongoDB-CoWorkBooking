package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.infrastructure.web.dto.RegisterDto;
import com.dacm.hexagonal.infrastructure.web.security.jwt.JwtLoginResponse;

public interface RegisterService {


    JwtLoginResponse signUp(RegisterDto request);
}
