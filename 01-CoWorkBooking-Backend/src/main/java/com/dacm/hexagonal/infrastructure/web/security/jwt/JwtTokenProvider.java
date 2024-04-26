package com.dacm.hexagonal.infrastructure.web.security.jwt;

import com.dacm.hexagonal.common.ApiResponse;
import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.common.Response;
import com.dacm.hexagonal.infrastructure.persistence.entity.UserEntity;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.util.HashMap;


@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String SECRET_KEY;

    public String getToken(UserEntity user) {
        return getToken(new HashMap<>(), user);
    }

    private String getToken(HashMap<String,Object> extraClaims, UserEntity user) {
        return Jwts
                .builder()
                .claims(extraClaims)
                .claim("firstName", user.getFirstName())
                .claim("lastName", user.getLastName())
                .subject(user.getUsername())
                .issuedAt(new java.util.Date(System.currentTimeMillis()))
                .expiration(new java.util.Date(System.currentTimeMillis() + 1000 * 60 * 60))
                .signWith(getKey())
                .compact();
    }

    public Claims getAllClaims(String token) {
        try {
            return Jwts
                    .parser()
                    .verifyWith(getKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(null, null, Message.JWT_TOKEN_EXPIRED);
        }

    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
