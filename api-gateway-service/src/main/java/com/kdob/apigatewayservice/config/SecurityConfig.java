package com.kdob.apigatewayservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http
                .authorizeExchange(exchanges -> exchanges
                        // Allow access to actuator endpoints
                        .pathMatchers("/actuator/**").permitAll()
                        // Allow access to authorization server
                        .pathMatchers("/oauth2/**", "/login/**").permitAll()

                        // ADMIN role has full access to all HTTP methods
                        .pathMatchers(HttpMethod.POST, "/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/**").hasRole("ADMIN")

                        // Both ADMIN and USER can perform GET requests
                        .pathMatchers(HttpMethod.GET, "/**").hasAnyRole("ADMIN", "USER")

                        // All other requests need authentication
                        .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(grantedAuthoritiesExtractor()))
                )
                .csrf(ServerHttpSecurity.CsrfSpec::disable);

        return http.build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverterAdapter grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new CustomJwtGrantedAuthoritiesConverter());
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }
}