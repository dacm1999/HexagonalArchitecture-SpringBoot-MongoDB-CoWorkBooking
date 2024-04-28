package com.dacm.hexagonal.infrastructure.web.security.jwt;

import com.dacm.hexagonal.common.Message;
import com.dacm.hexagonal.domain.exception.HandlerException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {


    private final JwtServiceImpl jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            final String token = getTokenFromRequest(request);
            final String username;

            if (token == null) {
                filterChain.doFilter(request, response);
                return;
            }
            username = jwtService.getUsernameFromToken(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username); // Load user details from userDetailsService using the provided username

                // Check if the token is valid for the loaded user details
                if (jwtService.isTokenValid(token, userDetails)) {
                    // Create an authentication token using the loaded user details and set it in the security context
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); // Set additional authentication token details

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);// Set the created authentication token in the security context
                }
            }
        } catch (ExpiredJwtException e) {
            sendJsonErrorResponse(response, Message.JWT_TOKEN_EXPIRED, HttpServletResponse.SC_UNAUTHORIZED);
            return;
        } catch (IOException e) {
            throw new IOException(Message.JWT_TOKEN_INVALID);
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");

        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer")) {
            return authHeader.substring(7);
        }

        return null;
    }

    private void sendJsonErrorResponse(HttpServletResponse response, String message, int status) throws IOException {
        HandlerException error = new HandlerException();
        error.setStatusCode(status);
        error.setMessage(message);
        error.setStatus(HttpStatus.valueOf(status));
        error.setTimestamp(LocalDateTime.now());

        response.setStatus(status);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        response.getWriter().write(mapper.writeValueAsString(error));
        response.getWriter().flush();
    }

}
