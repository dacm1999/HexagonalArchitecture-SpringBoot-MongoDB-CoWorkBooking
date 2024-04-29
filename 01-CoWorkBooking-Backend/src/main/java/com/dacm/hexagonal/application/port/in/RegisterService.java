package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.infrastructure.web.dto.RegisterDto;
import com.dacm.hexagonal.infrastructure.web.response.JwtLoginResponse;

public interface RegisterService {


    JwtLoginResponse signUp(RegisterDto request);
}
