package com.dacm.hexagonal.infrastructure.config.security;

import com.dacm.hexagonal.infrastructure.config.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Autowired
    private final JwtAuthenticationFilter jwtTokenProvider;
    @Autowired
    private final AuthenticationProvider authProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authRequest ->
                        authRequest
                                //Register endpoint
                                .requestMatchers("api/v1/register").permitAll()

                                //Login endpoint
                                .requestMatchers("api/v1/login/auth").permitAll()
                                .requestMatchers("api/v1/login/logout").hasRole("ADMIN")

                                //User endpoint
                                .requestMatchers("api/v1/users/create").hasAnyRole("ADMIN")
                                .requestMatchers("api/v1/users/createMultiple").hasRole("ADMIN")
                                .requestMatchers("api/v1/users/find/{userId}").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("api/v1/users/update/{userId}").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("api/v1/users/delete/{userId}").hasRole("ADMIN")
                                .requestMatchers("api/v1/users/allUsers").hasRole("ADMIN")

                                //Space endpoint
                                .requestMatchers("api/v1/spaces/create").hasAnyRole("ADMIN")
                                .requestMatchers("api/v1/spaces/createMultiple").hasRole("ADMIN")
                                .requestMatchers("api/v1/spaces/find/{spaceName}").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("api/v1/spaces/all").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("api/v1/spaces/delete/{spaceName}").hasRole("ADMIN")
                                .requestMatchers("api/v1/spaces/update/{spaceName}").hasAnyRole("ADMIN")
                                .requestMatchers("api/v1/spaces/allSpaceNames").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("api/v1/spaces/allAvailable").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("api/v1/spaces/allUnAvailable").hasAnyRole("ADMIN", "USER")

                                //Booking endpoint
                                .requestMatchers("api/v1/bookings/create").hasAnyRole("ADMIN", "MANAGER", "USER")
                                .requestMatchers("api/v1/bookings/createMultiple").hasAnyRole("ADMIN", "MANAGER")
                                .requestMatchers("api/v1/bookings/confirm/{bookingId}").hasAnyRole("ADMIN","MANAGER", "USER")
                                .requestMatchers("api/v1/bookings/cancel/{bookingId}").hasAnyRole("ADMIN","MANAGER", "USER")
                                .requestMatchers("api/v1/bookings/user/{userId}").hasAnyRole("ADMIN", "USER")
                                .requestMatchers("api/v1/bookings/all").hasRole("ADMIN")
                                .requestMatchers("api/v1/bookings/allByStartDate/{startDate}").hasAnyRole("ADMIN", "MANAGER", "USER")
                                .requestMatchers("api/v1/bookings/availableHours/{spaceId}/{date}").hasAnyRole("ADMIN", "MANAGER")
                                .requestMatchers("api/v1/bookings/status/{status}").hasAnyRole("ADMIN", "MANAGER")
                                .requestMatchers("api/v1/bookings/update/{bookingId}").hasAnyRole("ADMIN", "MANAGER", "USER")
                                .requestMatchers("api/v1/bookings/delete/{bookingId}").hasRole("ADMIN")
                                .requestMatchers("api/v1/bookings/find/{bookingId}").hasAnyRole("ADMIN")
                                .anyRequest().authenticated()
                )
                .sessionManagement(sessionManager -> sessionManager
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authProvider)
                .addFilterBefore(jwtTokenProvider, UsernamePasswordAuthenticationFilter.class)
                .build();
    }

}
