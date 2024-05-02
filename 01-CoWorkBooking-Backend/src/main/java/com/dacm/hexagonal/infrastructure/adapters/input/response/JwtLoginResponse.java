package com.dacm.hexagonal.infrastructure.adapters.input.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JwtLoginResponse {

    String token;
}
