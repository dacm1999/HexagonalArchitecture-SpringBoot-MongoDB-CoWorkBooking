package com.dacm.hexagonal.infrastructure.web.security.jwt;

import com.dacm.hexagonal.application.port.in.JwtService;
import io.jsonwebtoken.Claims;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.function.Function;

@Service
public class JwtServiceImpl implements JwtService{

    private JwtTokenProvider jwtTokenProvider;

    @Override
    public String getUsernameFromToken(String token) {
        return getClaim(token, Claims::getSubject);
    }

    @Override
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    @Override
    public <T> T getClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = jwtTokenProvider.getAllClaims(token);
        return claimsResolver.apply(claims);
    }

    @Override
    public Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    @Override
    public boolean isTokenExpired(String token) {
        return getExpiration(token).before(new Date());
    }
}
