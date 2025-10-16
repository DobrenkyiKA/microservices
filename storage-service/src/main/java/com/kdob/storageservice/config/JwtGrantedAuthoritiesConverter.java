package com.kdob.storageservice.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.*;

public class JwtGrantedAuthoritiesConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    @Override
    public Collection<GrantedAuthority> convert(Jwt jwt) {
        // Start with any explicit authorities claim
        Collection<String> authorities = Optional.ofNullable(jwt.getClaimAsStringList("authorities"))
                .orElse(Collections.emptyList());

        Set<GrantedAuthority> result = new LinkedHashSet<>();
        for (String auth : authorities) {
            if (auth != null && !auth.isBlank()) {
                result.add(new SimpleGrantedAuthority(auth));
            }
        }

        // Add OAuth2 scopes as SCOPE_* authorities (supports both string and collection formats)
        for (String scope : extractScopes(jwt)) {
            if (scope != null && !scope.isBlank()) {
                result.add(new SimpleGrantedAuthority("SCOPE_" + scope));
            }
        }

        return result;
    }

    private List<String> extractScopes(Jwt jwt) {
        List<String> scopes = new ArrayList<>();
        Object scopeClaim = jwt.getClaims().get("scope");
        if (scopeClaim instanceof String s) {
            if (!s.isBlank()) {
                scopes.addAll(Arrays.stream(s.split(" "))
                        .map(String::trim)
                        .filter(str -> !str.isEmpty())
                        .toList());
            }
        } else if (scopeClaim instanceof Collection<?> c) {
            for (Object o : c) {
                if (o != null) {
                    String s = o.toString().trim();
                    if (!s.isEmpty()) scopes.add(s);
                }
            }
        }
        // Fallback to 'scp' claim (some providers)
        if (scopes.isEmpty()) {
            Object scpClaim = jwt.getClaims().get("scp");
            if (scpClaim instanceof Collection<?> c) {
                for (Object o : c) {
                    if (o != null) {
                        String s = o.toString().trim();
                        if (!s.isEmpty()) scopes.add(s);
                    }
                }
            }
        }
        return scopes;
    }
}