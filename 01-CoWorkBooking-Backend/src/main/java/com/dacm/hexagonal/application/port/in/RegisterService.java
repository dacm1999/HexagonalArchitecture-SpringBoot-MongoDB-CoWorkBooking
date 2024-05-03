package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.domain.model.dto.RegisterDto;
import com.dacm.hexagonal.infrastructure.adapters.input.response.JwtLoginResponse;

public interface RegisterService {


    JwtLoginResponse signUp(RegisterDto request);
}
