//package com.kdob.apigatewayservice.config;
//
//import org.springframework.cloud.gateway.route.RouteLocator;
//import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class GatewayConfig {
//
//    @Bean
//    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
//        return builder.routes()
//                // Route for song-service
//                .route("song-service", r -> r
//                        .path("/songs/**")
//                        .uri("lb://song-service"))
//
//                // Route for resource-service
//                .route("resource-service", r -> r
//                        .path("/resources/**")
//                        .uri("lb://resource-service"))
//
//                // Route for storage-service
//                .route("storage-service", r -> r
//                        .path("/storages/**")
//                        .uri("lb://storage-service"))
//
//                // Route for resource-processor
//                .route("resource-processor", r -> r
//                        .path("/processor/**")
//                        .uri("lb://resource-processor"))
//
//                .build();
//    }
//}