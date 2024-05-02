package com.dacm.hexagonal.application.port.in;

import com.dacm.hexagonal.domain.model.Login;
import com.dacm.hexagonal.infrastructure.adapters.input.response.JwtLoginResponse;

public interface LoginService {

    JwtLoginResponse login(Login request);
}
