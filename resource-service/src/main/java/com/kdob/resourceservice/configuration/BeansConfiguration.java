package com.kdob.resourceservice.configuration;

import lombok.AllArgsConstructor;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.server.resource.authentication.BearerTokenAuthentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;

@Configuration
@EnableRetry
@AllArgsConstructor
@RefreshScope
public class BeansConfiguration {

    private final CustomRetryListener customRetryListener;

    @Bean
    public RestClient restClient(RestClient.Builder builder, OAuth2AuthorizedClientManager authorizedClientManager) {
        // Interceptor: propagate current Bearer token; if absent, obtain client-credentials token
        ClientHttpRequestInterceptor oauth2OrBearerInterceptor = (request, body, execution) -> {
            if (!request.getHeaders().containsKey("Authorization")) {
                String token = resolveBearerToken();
                if (StringUtils.hasText(token)) {
                    request.getHeaders().setBearerAuth(token);
                } else {
                    OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest
                            .withClientRegistrationId("service-client")
                            .principal("resource-service")
                            .build();
                    var authorizedClient = authorizedClientManager.authorize(authorizeRequest);
                    if (authorizedClient == null || authorizedClient.getAccessToken() == null) {
                        throw new IllegalStateException("Failed to acquire access token for client 'service-client'");
                    }
                    request.getHeaders().setBearerAuth(authorizedClient.getAccessToken().getTokenValue());
                }
            }
            return execution.execute(request, body);
        };
        return builder
                .requestInterceptor(oauth2OrBearerInterceptor)
                .build();
    }

    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(
            ClientRegistrationRepository clientRegistrationRepository,
            OAuth2AuthorizedClientService authorizedClientService) {
        OAuth2AuthorizedClientProvider authorizedClientProvider =
                OAuth2AuthorizedClientProviderBuilder.builder()
                        .clientCredentials()
                        .build();
        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager =
                new AuthorizedClientServiceOAuth2AuthorizedClientManager(
                        clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);
        return authorizedClientManager;
    }

    private String resolveBearerToken() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof JwtAuthenticationToken jwtAuth) {
            return jwtAuth.getToken() != null ? jwtAuth.getToken().getTokenValue() : null;
        }
        if (authentication instanceof BearerTokenAuthentication bearer) {
            return bearer.getToken() != null ? bearer.getToken().getTokenValue() : null;
        }
        return null;
    }

    @Bean
    public RetryTemplate retryTemplate() {
        return RetryTemplate.builder()
                .maxAttempts(4)
                .exponentialBackoff(1000, 2.0, 10000)
                .withListener(customRetryListener)
                .build();
    }
}
