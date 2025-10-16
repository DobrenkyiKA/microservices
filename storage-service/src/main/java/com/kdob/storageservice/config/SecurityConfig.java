package com.kdob.storageservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(authorize -> authorize
                        // Allow actuator endpoints
                        .requestMatchers("/actuator/**").permitAll()

                        // Allow service-to-service (client credentials) tokens with scopes for storage endpoints
                        .requestMatchers(HttpMethod.POST, "/**").hasAnyAuthority("SCOPE_write", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/**").hasAnyAuthority("SCOPE_write", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/**").hasAnyAuthority("SCOPE_write", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/**").hasAnyAuthority("SCOPE_write", "ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/**").hasAnyAuthority("SCOPE_read", "ROLE_ADMIN", "ROLE_USER")

                        // All other requests need authentication
                        .anyRequest().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                )
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .csrf(csrf -> csrf.disable());

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(new JwtGrantedAuthoritiesConverter());
        return converter;
    }
}